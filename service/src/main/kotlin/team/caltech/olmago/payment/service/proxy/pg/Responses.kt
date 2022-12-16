package team.caltech.olmago.payment.service.proxy.pg

data class PgPayResponse(
  val paymentId: Long,
  val pgPaymentId: String,
  val isSuccess: Boolean,
  val failedReasonMessage: String?
)

data class PgRefundResponse(
  val paymentId: Long,
  val pgPaymentId: String,
  val isSuccess: Boolean,
  val failedReasonMessage: String?
)
