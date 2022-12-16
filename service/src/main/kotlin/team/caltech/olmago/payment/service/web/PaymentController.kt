package team.caltech.olmago.payment.service.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.caltech.olmago.payment.service.dto.PaymentRequestCommand
import team.caltech.olmago.payment.service.proxy.pg.PaymentGateway
import team.caltech.olmago.payment.service.proxy.pg.PgPayCommand
import team.caltech.olmago.payment.service.service.PaymentInformationService
import team.caltech.olmago.payment.service.service.PaymentService
import java.time.LocalDateTime

@RestController
@RequestMapping("/olmago/api/v1/payment")
class PaymentController(
  val paymentService: PaymentService,
  val paymentInformationService: PaymentInformationService,
  val paymentGateway: PaymentGateway
) {
  @PostMapping
  fun pay(@RequestBody cmd: PaymentRequestCommand): ResponseEntity<Long> {
    val paymentRequestResponse = paymentService.requestPayment(cmd)
    paymentGateway.pay(
      PgPayCommand(
        cardNumber = paymentRequestResponse.cardNumber,
        payRequestDateTime = LocalDateTime.now(),
        payAmount = cmd.amount,
        paymentId = paymentRequestResponse.paymentId
      )
    )
    return ResponseEntity.ok().body(paymentRequestResponse.paymentId)
  }
}