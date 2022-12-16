package team.caltech.olmago.payment.service.web

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import team.caltech.olmago.payment.service.createPaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.createPaymentInformationResponse
import team.caltech.olmago.payment.service.dto.PaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.service.PaymentInformationService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PaymentInformationControllerTest() : StringSpec() {
  override fun extensions() = listOf(SpringExtension)

  @Autowired
  lateinit var webTestClient: WebTestClient

  @MockkBean
  private lateinit var service: PaymentInformationService

  init {
    "결제정보 등록 API를 호출하면 결제관리번호를 반환한다." {
      every { service.registerPaymentInformation(any()) } returns 1

      val cmd = createPaymentInformationRegisterCommand()
      createPaymentInformation(cmd)
        .expectStatus().isOk
        .expectBody().shouldNotBeNull()
    }

    "결제정보 등록된 상태에서 결제정보 조회 API를 호출하면 결제정보를 반환한다." {
      val response = createPaymentInformationResponse()
      every { service.searchPaymentInformation(any()) } returns response

      searchPaymentInformation(response.paymentInformationId)
        .expectStatus().isOk
        .expectBody()
          .jsonPath("$.customerId").isEqualTo(response.customerId)
          .jsonPath("$.cardNumber").isEqualTo(response.cardNumber)
          .jsonPath("$.cardCompanyCode").isEqualTo(response.cardCompanyCode)
          .jsonPath("$.cardCompanyName").isEqualTo(response.cardCompanyName)
          .jsonPath("$.contractsInformation").isEqualTo(response.contractsInformation)
    }

    "없는 결제정보로 조회 API를 호출하면 404오류가 발생한다." {
      every { service.searchPaymentInformation(any()) } throws IllegalArgumentException()
      searchPaymentInformation(1)
        .expectStatus().isNotFound
    }

    afterContainer {
      clearAllMocks()
    }
  }

  fun createPaymentInformation(cmd: PaymentInformationRegisterCommand) =
    webTestClient.post()
      .uri("/olmago/api/v1/payment-information")
      .body(BodyInserters.fromValue(cmd))
      .accept(APPLICATION_JSON)
      .exchange()

  fun searchPaymentInformation(id: Long) =
    webTestClient.get()
      .uri("/olmago/api/v1/payment-information/{id}", id)
      .accept(APPLICATION_JSON)
      .exchange()
}