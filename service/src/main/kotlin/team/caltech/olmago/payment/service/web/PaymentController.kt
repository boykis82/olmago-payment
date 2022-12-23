package team.caltech.olmago.payment.service.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.caltech.olmago.payment.service.dto.*
import team.caltech.olmago.payment.service.service.PaymentInformationService
import team.caltech.olmago.payment.service.service.PaymentService

@RestController
@RequestMapping("/olmago/api/v1/payment")
class PaymentController(
  val paymentService: PaymentService,
  val paymentInformationService: PaymentInformationService
) {
  @PostMapping
  fun requestPayment(@RequestBody cmd: PaymentRequestCommand): ResponseEntity<Long> {
    return try {
      val body = paymentService.requestPayment(cmd)
      ResponseEntity.ok().body(body)
    } catch (e: Exception) {
      ResponseEntity.badRequest().build()
    }
  }

  @PutMapping("/{paymentId}/complete")
  fun completePayment(
    @PathVariable paymentId: Long,
    @RequestBody cmd: PaymentCompleteCommand
  ): ResponseEntity<Void> {
    return try {
      paymentService.completePayment(cmd)
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      ResponseEntity.badRequest().build()
    }
  }

  @PutMapping("/{paymentId}/fail")
  fun failPayment(
    @PathVariable paymentId: Long,
    @RequestBody cmd: PaymentFailCommand
  ): ResponseEntity<Void> {
    return try {
      paymentService.failPayment(cmd)
      ResponseEntity.ok().build()
    } catch (e: Exception) {
      ResponseEntity.badRequest().build()
    }
  }

  @GetMapping("/{paymentId}")
  fun searchOneDetailPaymentHistory(
    @PathVariable paymentId: Long
  ) : ResponseEntity<DetailPaymentHistoryResponse> {
    return try {
      val body = paymentService.searchOnePaymentHistory(paymentId)
      ResponseEntity.ok().body(body)
    } catch (e: Exception) {
      ResponseEntity.badRequest().build()
    }
  }

  @GetMapping
  fun searchSummaryPaymentHistory(
    @RequestParam paymentInformationId: Long
  ) : ResponseEntity<List<SummaryPaymentHistoryResponse>> {
    return try {
      val body = paymentService.searchPaymentHistories(paymentInformationId)
      ResponseEntity.ok().body(body)
    } catch (e: Exception) {
      ResponseEntity.badRequest().build()
    }
  }
}