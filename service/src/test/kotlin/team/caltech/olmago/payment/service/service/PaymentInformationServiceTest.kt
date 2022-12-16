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
import team.caltech.olmago.payment.service.createPaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.createPaymentInformationRegisterCommands
import java.time.LocalDateTime

@SpringBootTest
class PaymentInformationServiceTest : StringSpec() {
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

    "없는 id로 결제정보 조회 method를 호출하면 IllegalArgumentException이 발생한다." {
      val ex = shouldThrow<IllegalArgumentException> {
        paymentInformationService.searchPaymentInformation(100)
      }
      ex shouldHaveMessage "PaymentInformation(100) not found!"
    }

    "결제정보가 다양한 고객번호로 여러개 등록된 상태에서 결제정보 건수별 상황 조회 시 예상한 건수만큼 데이터 반환된다." {
      createPaymentInformationRegisterCommands().map {
        paymentInformationService.registerPaymentInformation(it)
      }
      io.kotest.data.forAll(
        table(
          headers("customerId", "expectedSize"),
          row(1, 2),
          row(2, 1),
          row(3, 0)
        )
      ) { customerId: Long, expectedSize: Int ->
        paymentInformationService.searchPaymentInformationByCustomerId(customerId) shouldHaveSize expectedSize
      }
    }

    "계약 2개 물려 있는 결제정보에 계약 1건 해지하면 결제정보 조회 시 계약 1건만 조회된다." {
      val cmd = createPaymentInformationRegisterCommand()
      val id = paymentInformationService.registerPaymentInformation(cmd)
      paymentInformationService.unlinkContract(1, 2, LocalDateTime.now())

      val response = paymentInformationService.searchPaymentInformation(id)
      response.contractsInformation shouldBe "우주패스All"
    }

    "계약 2개 물려 있는 결제정보에 계약 2건 해지하면 결제정보 조회 시 계약 정보 조회안된다" {
      val cmd = createPaymentInformationRegisterCommand()
      val id = paymentInformationService.registerPaymentInformation(cmd)
      paymentInformationService.unlinkContract(1, 1, LocalDateTime.now())
      paymentInformationService.unlinkContract(1, 2, LocalDateTime.now())

      val response = paymentInformationService.searchPaymentInformation(id)
      response.contractsInformation shouldBe ""
    }
  }
}
