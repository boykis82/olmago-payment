package team.caltech.olmago.payment.service.dto

import java.time.LocalDateTime

data class PaymentInformationResponse(
  val paymentInformationId: Long,
  val customerId: Long,
  val cardNumber: String,
  val cardCompanyCode: String,
  val cardCompanyName: String,
  val contractsInformation: String,
  val cratedDateTime: LocalDateTime
)

data class PaymentHistoryPerContractResponse(
  val contractId: Long,
  val paymentAmount: Long
)

data class DetailPaymentHistoryResponse(
  val paymentId: Long,
  val paymentInformationId: Long,
  val paymentRequestDateTime: LocalDateTime,
  val paymentCompletedDateTime: LocalDateTime?,
  val paymentFailedDateTime: LocalDateTime?,
  val paymentFailedCauseMessage: String?,
  val paymentAmount: Long,
  val paymentStatus: String,
  val paymentHistoryPerContracts: List<PaymentHistoryPerContractResponse>
)

data class SummaryPaymentHistoryResponse(
  val paymentId: Long,
  val paymentInformationId: Long,
  val paymentRequestDateTime: LocalDateTime,
  val paymentCompletedDateTime: LocalDateTime?,
  val paymentFailedDateTime: LocalDateTime?,
  val paymentFailedCauseMessage: String?,
  val paymentAmount: Long,
  val paymentStatus: String,
)