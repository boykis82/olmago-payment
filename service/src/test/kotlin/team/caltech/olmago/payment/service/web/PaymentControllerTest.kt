package team.caltech.olmago.payment.service.web

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.clearAllMocks
import io.mockk.every
import org.mockito.ArgumentMatchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import team.caltech.olmago.payment.service.createPaymentRequestCommand
import team.caltech.olmago.payment.service.createPgPayCommand
import team.caltech.olmago.payment.service.createPgPayResponse
import team.caltech.olmago.payment.service.proxy.pg.PaymentGateway
import team.caltech.olmago.payment.service.proxy.pg.PgPayCommand
import team.caltech.olmago.payment.service.service.PaymentInformationService
import team.caltech.olmago.payment.service.service.PaymentRequestResponse
import team.caltech.olmago.payment.service.service.PaymentService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PaymentControllerTest : StringSpec() {
  override fun extensions() = listOf(SpringExtension)

  @Autowired
  lateinit var webTestClient: WebTestClient

  @MockkBean
  private lateinit var service: PaymentService

  @MockkBean
  private lateinit var pg: PaymentGateway

  init {
    "정상 정보로 결제 요청하면 결제번호 반환한다" {
      val cmd = createPaymentRequestCommand(1)
      val res = createPgPayResponse(paymentId = 1, isSuccess = true, failedReasonMessage = null)
      every { service.requestPayment(cmd) } returns PaymentRequestResponse(1, "1234567887654321")
      every { pg.pay(any()) } returns res

      webTestClient.post()
        .uri("/olmago/api/v1/payment")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
        .expectBody()
          .jsonPath("$").isEqualTo(1)
    }

    afterContainer {
      clearAllMocks()
    }
  }
}