package team.caltech.olmago.payment.service.proxy.pg

interface PaymentGateway {
  fun pay(cmd: PgPayCommand): PgPayResponse
  fun refund(cmd: PgRefundCommand): PgRefundResponse
}