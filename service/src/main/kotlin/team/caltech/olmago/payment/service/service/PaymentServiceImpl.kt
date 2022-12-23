package team.caltech.olmago.payment.service.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.caltech.olmago.payment.domain.*
import team.caltech.olmago.payment.service.dto.*
import java.time.LocalDateTime
import kotlin.math.truncate

@Service
class PaymentServiceImpl(
  val paymentInformationService: PaymentInformationService,
  val paymentRepository: PaymentRepository,
  val prodDcRevRepo: ProductDiscountRevenueRepository
  ) : PaymentService {

  @Transactional
  override fun requestPayment(cmd: PaymentRequestCommand): Long {
    paymentInformationService.searchPaymentInformation(cmd.paymentInformationId)
    val payment = Payment(
      LocalDateTime.now(),
      PaymentStatus.PAYMENT_AWAITING,
      cmd.amount,
      cmd.paymentInformationId
    )
    cmd.contractPaymentInformation.forEach {
      createContractPaymentDetails(it, payment)
    }
    return paymentRepository.save(payment).id!!
  }

  private fun createContractPaymentDetails(
    contractPaymentInformation: ContractPaymentInformation,
    payment: Payment
  ) =
    contractPaymentInformation.productPaymentInformation.forEach {
      createProductPaymentDetails(contractPaymentInformation, it, payment)
    }

  private fun createProductPaymentDetails(
    contractPaymentInformation: ContractPaymentInformation,
    productPaymentInformation: ProductPaymentInformation,
    payment: Payment
  ) {
    prodDcRevRepo
      .findByProductCodeAndDiscountPolicyCode(
        productPaymentInformation.productCode,
        null
      )
      .also { check(it.isNotEmpty()) }
      .forEach { createPaymentDetail(contractPaymentInformation, payment, it) }

    productPaymentInformation.discountPaymentInformation.forEach { dcPayInfo ->
      prodDcRevRepo
        .findByProductCodeAndDiscountPolicyCode(
          productPaymentInformation.productCode,
          dcPayInfo.discountPolicyCode
        )
        .also { check(it.isNotEmpty()) }
        .forEach { createPaymentDetail(contractPaymentInformation, payment, it) }
    }
  }

  private fun createPaymentDetail(
    contractPaymentInformation: ContractPaymentInformation,
    payment: Payment,
    revenuePer: ProductDiscountRevenue
  ) {
    val revenueItem = revenuePer.revenueItem
    if (revenueItem.isVatObject) {
      val vatAmount = revenuePer.separatedRevenueAmount.vat()
      payment.addPaymentDetail(
        PaymentDetail(payment, contractPaymentInformation.contractId, vatAmount, revenueItem.vatRevenueItemCode)
      )
      val supplyAmount = revenuePer.separatedRevenueAmount - vatAmount
      payment.addPaymentDetail(
        PaymentDetail(payment, contractPaymentInformation.contractId, supplyAmount, revenueItem.revenueItemCode)
      )
    } else {
      payment.addPaymentDetail(
        PaymentDetail(payment, contractPaymentInformation.contractId, revenuePer.separatedRevenueAmount, revenueItem.revenueItemCode)
      )
    }
  }

  private fun Long.vat() = truncate(this / 11.0).toLong()

  @Transactional(readOnly = true)
  override fun searchOnePaymentHistory(paymentId: Long): DetailPaymentHistoryResponse =
    paymentRepository
      .findPaymentWithDetails(paymentId)
      ?.toDetailPaymentHistoryResponse()
      ?: throw IllegalArgumentException("$paymentId not found!")

  @Transactional(readOnly = true)
  override fun searchPaymentHistories(paymentInformationId: Long): List<SummaryPaymentHistoryResponse> =
    paymentRepository
      .findByPaymentInformationId(paymentInformationId)
      .map { it.toSummaryPaymentHistoryResponse() }

  private fun Payment.toSummaryPaymentHistoryResponse(): SummaryPaymentHistoryResponse {
    return SummaryPaymentHistoryResponse(
      paymentId = id!!,
      paymentInformationId = paymentInformationId,
      paymentRequestDateTime = paymentRequestedDateTime,
      paymentCompletedDateTime = paymentCompletedDateTime,
      paymentFailedDateTime = paymentFailedDateTime,
      paymentFailedCauseMessage = paymentFailedCauseMessage,
      paymentAmount = amount,
      paymentStatus = paymentStatus.name,
    )
  }

  private fun Payment.toDetailPaymentHistoryResponse(): DetailPaymentHistoryResponse {
    val paymentHistoryPerContractResponse =
      paymentDetails
        .groupingBy { it.contractId }
        .eachSumBy { it.amount }
        .map { PaymentHistoryPerContractResponse(it.key, it.value) }

    return DetailPaymentHistoryResponse(
      paymentId = id!!,
      paymentInformationId = paymentInformationId,
      paymentRequestDateTime = paymentRequestedDateTime,
      paymentCompletedDateTime = paymentCompletedDateTime,
      paymentFailedDateTime = paymentFailedDateTime,
      paymentFailedCauseMessage = paymentFailedCauseMessage,
      paymentAmount = amount,
      paymentStatus = paymentStatus.name,
      paymentHistoryPerContracts = paymentHistoryPerContractResponse
    )
  }

  fun <T, K> Grouping<T, K>.eachSumBy(
    selector: (T) -> Long
  ): Map<K, Long> =
    fold(0) { acc, elem -> acc + selector(elem) }

  @Transactional
  override fun completePayment(cmd: PaymentCompleteCommand) =
    paymentRepository
      .findByIdOrNull(cmd.paymentId)
      ?.complete(cmd.paymentCompletedDateTime, cmd.amount)
      ?: throw IllegalStateException("Payment(${cmd.paymentId}) not exists!")

  @Transactional
  override fun failPayment(cmd: PaymentFailCommand) =
    paymentRepository
      .findByIdOrNull(cmd.paymentId)
      ?.fail(cmd.paymentFailedDateTime, cmd.paymentFailedCauseMessage)
      ?: throw IllegalStateException("Payment(${cmd.paymentId}) not exists!")
}