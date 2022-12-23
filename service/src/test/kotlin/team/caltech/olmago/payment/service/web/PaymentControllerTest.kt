package team.caltech.olmago.payment.service.web

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createDetailPaymentHistoryResponse
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentCompleteCommand
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentFailedCommand
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentRequestCommand
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createSummaryPaymentHistoriesResponse
import team.caltech.olmago.payment.service.service.PaymentService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PaymentControllerTest : StringSpec() {
  override fun extensions() = listOf(SpringExtension)

  @Autowired
  lateinit var webTestClient: WebTestClient

  @MockkBean
  private lateinit var service: PaymentService

  init {
    "정상 정보로 결제 요청하면 결제번호 반환한다" {
      val cmd = createPaymentRequestCommand(1)
      every { service.requestPayment(cmd) } returns 1

      webTestClient.post()
        .uri("/olmago/api/v1/payment")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
        .expectBody()
          .jsonPath("$").isEqualTo(1)
    }

    "없는 결제정보로 결제 요청하면 bad request 리턴한다." {
      val cmd = createPaymentRequestCommand(1)
      every { service.requestPayment(cmd) } throws(IllegalArgumentException())

      webTestClient.post()
        .uri("/olmago/api/v1/payment")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest
    }

    "정상적인 정보로 결제완료 호출하면 결제완료된다" {
      val paymentId = 1L
      val cmd = createPaymentCompleteCommand(paymentId, 1000)
      every { service.completePayment(cmd) } just Runs

      webTestClient.put()
        .uri("/olmago/api/v1/payment/$paymentId/complete")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
    }

    "비정상적인 정보로 결제완료 호출하면 bad reqeust 리턴한다." {
      val paymentId = 1L
      val cmd = createPaymentCompleteCommand(paymentId, 1000)
      every { service.completePayment(cmd) } throws(IllegalArgumentException())

      webTestClient.put()
        .uri("/olmago/api/v1/payment/$paymentId/complete")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest
    }

    "정상적인 정보로 결제실패 호출하면 결제실패 성공한다" {
      val paymentId = 1L
      val cmd = createPaymentFailedCommand(paymentId)
      every { service.failPayment(cmd) } just Runs

      webTestClient.put()
        .uri("/olmago/api/v1/payment/$paymentId/fail")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
    }

    "비정상적인 정보로 결제실패 호출하면 결제실패 실패한다" {
      val paymentId = 1L
      val cmd = createPaymentFailedCommand(paymentId)
      every { service.failPayment(cmd) } throws(IllegalArgumentException())

      webTestClient.put()
        .uri("/olmago/api/v1/payment/$paymentId/fail")
        .body(BodyInserters.fromValue(cmd))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest
    }

    "존재하는 결제ID로 결제정보 단건 조회하면 결제정보 반환한다" {
      val paymentId = 1L
      val mockResponse = createDetailPaymentHistoryResponse(paymentId)
      every { service.searchOnePaymentHistory(1) } returns mockResponse

      webTestClient.get()
        .uri("/olmago/api/v1/payment/$paymentId")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
        .expectBody()
          .jsonPath("$.paymentId").isEqualTo(mockResponse.paymentId)
          .jsonPath("$.paymentInformationId").isEqualTo(mockResponse.paymentInformationId)
          .jsonPath("$.paymentAmount").isEqualTo(mockResponse.paymentAmount)
          .jsonPath("$.paymentStatus").isEqualTo(mockResponse.paymentStatus)
          .jsonPath("$.paymentHistoryPerContracts.length()").isEqualTo(mockResponse.paymentHistoryPerContracts.size)
          .jsonPath("$.paymentHistoryPerContracts[0].contractId").isEqualTo(mockResponse.paymentHistoryPerContracts[0].contractId)
          .jsonPath("$.paymentHistoryPerContracts[1].contractId").isEqualTo(mockResponse.paymentHistoryPerContracts[1].contractId)
          .jsonPath("$.paymentHistoryPerContracts[0].paymentAmount").isEqualTo(mockResponse.paymentHistoryPerContracts[0].paymentAmount)
          .jsonPath("$.paymentHistoryPerContracts[1].paymentAmount").isEqualTo(mockResponse.paymentHistoryPerContracts[1].paymentAmount)
    }

    "존재하지 않는 결제ID로 결제정보 단건 조회하면 not found 반환한다" {
      every { service.searchOnePaymentHistory(1) } throws IllegalArgumentException()
      webTestClient.get()
        .uri("/olmago/api/v1/payment/1")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest
    }

    "결제정보ID로 결제정보 목록 조회하면 결제정보 목록 반환한다" {
      val mockResponse = createSummaryPaymentHistoriesResponse(3)
      every { service.searchPaymentHistories(1L) } returns mockResponse

      webTestClient.get()
        .uri("/olmago/api/v1/payment?paymentInformationId=1")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.length()").isEqualTo(3)
    }

    afterContainer {
      clearAllMocks()
    }
  }
}