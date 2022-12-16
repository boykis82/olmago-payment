package team.caltech.olmago.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OlmagoPaymentApplication

fun main(args: Array<String>) {
  runApplication<OlmagoPaymentApplication>(*args)
}
