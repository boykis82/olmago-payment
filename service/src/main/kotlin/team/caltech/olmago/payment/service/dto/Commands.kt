package team.caltech.olmago.payment.service.dto

import java.time.LocalDateTime

data class Contract(
  val contractId: Long,
  val productName: String
)

data class PaymentInformationRegisterCommand(
  val customerId: Long,
  val contracts: List<Contract>,
  val cardNumber: String,
  val cardCompanyCode: String
)

data class DiscountPaymentInformation(
  val discountPolicyCode: String,
  val amount: Long
)

data class ProductPaymentInformation(
  val productCode: String,
  val amount: Long,
  val discountPaymentInformation: List<DiscountPaymentInformation>
)

data class ContractPaymentInformation(
  val contractId: Long,
  val amount: Long,
  val productPaymentInformation: List<ProductPaymentInformation>
)

data class PaymentRequestCommand(
  val paymentInformationId: Long,
  val amount: Long,
  val contractPaymentInformation: List<ContractPaymentInformation>
)

data class PaymentCompleteCommand(
  val paymentId: Long,
  val paymentCompletedDateTime: LocalDateTime,
  val amount: Long
)

data class PaymentFailCommand(
  val paymentId: Long,
  val paymentFailedDateTime: LocalDateTime,
  val paymentFailedCauseMessage: String
)