package team.caltech.olmago.payment.domain

enum class PaymentStatus {
  PAYMENT_AWAITING,
  PAYMENT_COMPLETED,
  PAYMENT_FAILED,
  REFUND_AWAITING,
  REFUND_COMPLETED
}