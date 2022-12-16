package team.caltech.olmago.payment.service.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.caltech.olmago.payment.domain.*
import team.caltech.olmago.payment.service.dto.PaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.dto.PaymentInformationResponse
import java.time.LocalDateTime

@Service
class PaymentInformationServiceImpl(
  val paymentInformationRepository: PaymentInformationRepository
  ) : PaymentInformationService {

  @Transactional
  override fun registerPaymentInformation(cmd: PaymentInformationRegisterCommand): Long =
    paymentInformationRepository.save(cmd.toEntity()).id!!

  private fun PaymentInformationRegisterCommand.toEntity() : PaymentInformation {
    val now = LocalDateTime.now()
    val paymentInformation = PaymentInformation(
      customerId,
      cardNumber,
      CardCompany.valueOf(cardCompanyCode),
      now
    )
    contracts
      .map { PaymentInformationDetail(it.contractId, it.productName, paymentInformation, now) }
      .forEach(paymentInformation::addPaymentInformationDetail)

    return paymentInformation
  }

  @Transactional(readOnly = true)
  override fun searchPaymentInformation(id: Long): PaymentInformationResponse =
    paymentInformationRepository
      .findByIdWithValidContracts(id)
      ?.toPaymentInformationResponse()
      ?: throw IllegalArgumentException("PaymentInformation($id) not found!")

  private fun PaymentInformation.toPaymentInformationResponse() =
    PaymentInformationResponse(
      id!!,
      customerId,
      cardNumber,
      cardCompany.name,
      cardCompany.koreanName,
      concatContractsInformation(),
      createdDateTime
    )

  private fun PaymentInformation.concatContractsInformation(): String {
    return paymentInformationDetails
      .filter { it -> it.endDateTime == null }
      .joinToString(",") { it.productName }
  }

  @Transactional(readOnly = true)
  override fun searchPaymentInformationByCustomerId(customerId: Long): List<PaymentInformationResponse> =
    paymentInformationRepository
      .findByCustomerId(customerId)
      .map { it.toPaymentInformationResponse() }

  @Transactional
  override fun unlinkContract(customerId: Long, contractId: Long, unlinkDateTime: LocalDateTime) =
    paymentInformationRepository
      .findByCustomerAndContractId(customerId, contractId)
      ?.unlinkContract(contractId, unlinkDateTime)
      ?: throw IllegalArgumentException("$contractId not found!")
}