package team.caltech.olmago.payment.service.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import team.caltech.olmago.payment.domain.*
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.TestSpecificLanguage.Companion.createPaymentRequestCommand
import team.caltech.olmago.payment.service.dto.*
import java.time.LocalDateTime

@SpringBootTest
class PaymentQueryTest : StringSpec() {
  override fun extensions() = listOf(SpringExtension)

  @Autowired
  lateinit var paymentService: PaymentService

  @Autowired
  lateinit var paymentRepository: PaymentRepository

  @Autowired
  lateinit var productDiscountRevenueRepository: ProductDiscountRevenueRepository

  @Autowired
  lateinit var revenueRepository: RevenueRepository

  @Autowired
  lateinit var paymentInformationService: PaymentInformationService

  @Autowired
  lateinit var paymentInformationRepository: PaymentInformationRepository

  override suspend fun beforeEach(testCase: TestCase) {
    revenueRepository.saveAll(
      listOf(
        RevenueItem("NR00001", "우주패스All_SKT", true, "NRZ0001"),
        RevenueItem("NR00002", "우주패스All_11번가", true, "NRZ0002"),
        RevenueItem("NR00003", "우주패스All_Amazon", true, "NRZ0003"),
        RevenueItem("NR00004", "우주패스All_Google", true, "NRZ0004"),
        RevenueItem("NR00005", "배달의민족", true, "NRZ0005"),
        RevenueItem("NRD0001", "우주패스All_SKT_이동전화연계할인", true, "NRZ0001"),
        RevenueItem("NRD0002", "우주패스All_Amazon_기본혜택", true, "NRZ0003"),
        RevenueItem("NRD0003", "우주패스All_Google_기본혜택", true, "NRZ0004"),
        RevenueItem("NRD0004", "배달의민족_옵션할인", true, "NRZ0001"),
      )
    )

    productDiscountRevenueRepository.saveAll(
      listOf(
        ProductDiscountRevenue("NMP0000001", null, revenueRepository.findByIdOrNull("NR00001")!!, 5500),
        ProductDiscountRevenue("NMP0000001", null, revenueRepository.findByIdOrNull("NR00002")!!, 4400),
        ProductDiscountRevenue("NMB0000001", null, revenueRepository.findByIdOrNull("NR00003")!!, 2000),
        ProductDiscountRevenue("NMB0000002", null, revenueRepository.findByIdOrNull("NR00004")!!, 2000),
        ProductDiscountRevenue("NMO0000001", null, revenueRepository.findByIdOrNull("NR00005")!!, 5000),
        ProductDiscountRevenue("NMP0000001", "DC00000001", revenueRepository.findByIdOrNull("NRD0001")!!, -5000),
        ProductDiscountRevenue("NMB0000001", "DC00000002", revenueRepository.findByIdOrNull("NRD0002")!!, -2000),
        ProductDiscountRevenue("NMB0000002", "DC00000002", revenueRepository.findByIdOrNull("NRD0003")!!, -2000),
        ProductDiscountRevenue("NMO0000001", "DC00000003", revenueRepository.findByIdOrNull("NRD0004")!!, -5000),
      )
    )
  }

  override suspend fun afterEach(testCase: TestCase, result: TestResult) {
    productDiscountRevenueRepository.deleteAll()
    revenueRepository.deleteAll()
    paymentRepository.deleteAll()
    paymentInformationRepository.deleteAll()
  }

  init {
    "결제정보ID로 결제내역조회하면 결제내역목록을 결제일 내림차순으로 반환한다." {
      val paymentInformationId = paymentInformationService.registerPaymentInformation(createPaymentInformationRegisterCommand())
      val paymentTimes = 3
      for (i in 1..paymentTimes) {
        paymentService.requestPayment(createPaymentRequestCommand(paymentInformationId))
      }
      val response = paymentService.searchPaymentHistories(paymentInformationId)
      response.size shouldBe paymentTimes
    }
  }
}