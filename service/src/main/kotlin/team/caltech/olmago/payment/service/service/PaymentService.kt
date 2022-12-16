package team.caltech.olmago.payment.service.service

import team.caltech.olmago.payment.service.dto.CompletePaymentCommand
import team.caltech.olmago.payment.service.dto.FailPaymentCommand
import team.caltech.olmago.payment.service.dto.PaymentHistoryResponse
import team.caltech.olmago.payment.service.dto.PaymentRequestCommand

data class PaymentRequestResponse(
  val paymentId: Long,
  val cardNumber: String
)

interface PaymentService {
  fun requestPayment(cmd: PaymentRequestCommand): PaymentRequestResponse
  fun searchPaymentHistory(id: Long): PaymentHistoryResponse
  fun completePayment(cmd: CompletePaymentCommand)
  fun failPayment(cmd: FailPaymentCommand)
}