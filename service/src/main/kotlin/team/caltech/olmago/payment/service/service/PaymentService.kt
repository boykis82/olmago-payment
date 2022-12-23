package team.caltech.olmago.payment.service.service

import team.caltech.olmago.payment.service.dto.*

interface PaymentService {
  fun requestPayment(cmd: PaymentRequestCommand): Long
  fun searchOnePaymentHistory(paymentId: Long): DetailPaymentHistoryResponse
  fun completePayment(cmd: PaymentCompleteCommand)
  fun failPayment(cmd: PaymentFailCommand)
  fun searchPaymentHistories(paymentInformationId: Long): List<SummaryPaymentHistoryResponse>
}