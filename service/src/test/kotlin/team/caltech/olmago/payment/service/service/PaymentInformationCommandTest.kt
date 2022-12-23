package team.caltech.olmago.payment.service.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.caltech.olmago.payment.domain.PaymentInformationRepository
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentInformationRegisterCommands
import java.time.LocalDateTime

@SpringBootTest
class PaymentInformationCommandTest : StringSpec() {
  override fun extensions() = listOf(SpringExtension)

  @Autowired
  lateinit var paymentInformationService: PaymentInformationService

  @Autowired
  lateinit var paymentInformationRepository: PaymentInformationRepository

  override suspend fun afterEach(testCase: TestCase, result: TestResult) =
    paymentInformationRepository.deleteAll()

  init {
    "결제정보 등록 method를 호출하면 결제관리번호를 반환한다." {
      val cmd = createPaymentInformationRegisterCommand()
      paymentInformationService.registerPaymentInformation(cmd) shouldBeGreaterThan 0
    }

    "결제정보 등록된 상태에서 결제정보 조회 method를 호출하면 결제정보를 반환한다." {
      val cmd = createPaymentInformationRegisterCommand()
      val id = paymentInformationService.registerPaymentInformation(cmd)
      val response = paymentInformationService.searchPaymentInformation(id)
      response.cardCompanyCode shouldBe cmd.cardCompanyCode
      response.cardCompanyName shouldBe "신한카드"
      response.customerId shouldBe cmd.customerId
      response.cardNumber shouldBe cmd.cardNumber
      response.cratedDateTime shouldHaveSameDayAs LocalDateTime.now()
      response.contractsInformation shouldBe "우주패스All,배달의민족"
    }
  }
}
