package team.caltech.olmago.payment.service.proxy.pg

import java.time.LocalDateTime

data class PgPayCommand(
  val cardNumber: String,
  val payRequestDateTime: LocalDateTime,
  val payAmount: Long,
  val paymentId: Long
)

data class PgRefundCommand(
  val cardNumber: String,
  val payRefundDateTime: LocalDateTime,
  val refundAmount: Long,
  val paymentId: Long
)
