package team.caltech.olmago.payment.service.service

import team.caltech.olmago.payment.service.dto.PaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.dto.PaymentInformationResponse
import java.time.LocalDateTime

interface PaymentInformationService {
  fun registerPaymentInformation(cmd: PaymentInformationRegisterCommand): Long
  fun searchPaymentInformation(id: Long): PaymentInformationResponse
  fun searchPaymentInformationByCustomerId(customerId: Long): List<PaymentInformationResponse>
  fun unlinkContract(customerId: Long, contractId: Long, unlinkDateTime: LocalDateTime)
}