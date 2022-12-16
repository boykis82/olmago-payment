package team.caltech.olmago.payment.service.proxy.pg

import org.springframework.stereotype.Service

@Service
class PaymentGatewayImpl : PaymentGateway {
  override fun pay(cmd: PgPayCommand): PgPayResponse {
    TODO("Not yet implemented")
  }

  override fun refund(cmd: PgRefundCommand): PgRefundResponse {
    TODO("Not yet implemented")
  }
}