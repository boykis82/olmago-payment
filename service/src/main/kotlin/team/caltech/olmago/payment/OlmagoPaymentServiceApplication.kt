package team.caltech.olmago.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OlmagoPaymentServiceApplication

fun main(args: Array<String>) {
  runApplication<OlmagoPaymentServiceApplication>(*args)
}
