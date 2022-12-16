package team.caltech.olmago.payment.service.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.caltech.olmago.payment.service.dto.PaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.dto.PaymentInformationResponse
import team.caltech.olmago.payment.service.service.PaymentInformationService

@RestController
@RequestMapping("/olmago/api/v1/payment-information")
class PaymentInformationController(
  val paymentInformationService: PaymentInformationService
) {
  @PostMapping
  fun registerPaymentInformation(@RequestBody cmd: PaymentInformationRegisterCommand): ResponseEntity<Long> {
    val paymentInformationId = paymentInformationService.registerPaymentInformation(cmd)
    return ResponseEntity.ok().body(paymentInformationId)
  }

  @GetMapping("/{id}")
  fun searchPaymentInformation(@PathVariable id: Long): ResponseEntity<PaymentInformationResponse> {
    return try {
      val paymentInformationResponse = paymentInformationService.searchPaymentInformation(id)
      ResponseEntity.ok().body(paymentInformationResponse)
    } catch (e: IllegalArgumentException) {
      ResponseEntity.notFound().build()
    }
  }
}