package team.caltech.olmago.payment.service.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import team.caltech.olmago.payment.domain.*
import team.caltech.olmago.payment.service.createPaymentInformationRegisterCommand
import team.caltech.olmago.payment.service.createPaymentRequestCommand
import team.caltech.olmago.payment.service.dto.*
import java.time.LocalDateTime

@SpringBootTest
class PaymentServiceTest : StringSpec() {
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
    "결제요청을 받으면 미결제 상태로 결제내역 생성한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      
      // act
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)      
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId
      
      // assert
      val response = paymentService.searchPaymentHistory(paymentId)
      response.paymentId shouldBe paymentId
      response.paymentInformationId shouldBe createPaymentRequestCommand.paymentInformationId
      response.paymentAmount shouldBe createPaymentRequestCommand.amount
      response.paymentStatus shouldBe "PAYMENT_AWAITING"
      response.paymentHistoryPerContracts shouldHaveSize 2
      response.paymentHistoryPerContracts[0].paymentAmount shouldBe 4900
      response.paymentHistoryPerContracts[1].paymentAmount shouldBe 0
      response.paymentRequestDateTime shouldHaveSameDayAs LocalDateTime.now()
      response.paymentCompletedDateTime shouldBe null
    }

    "정상결제완료되면 paymentStatus를 결제완료로 변경한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId
      
      // act
      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount
      )
      paymentService.completePayment(completePaymentCommand)
      
      // assert
      val response = paymentService.searchPaymentHistory(paymentId)
      response.paymentId shouldBe paymentId
      response.paymentStatus shouldBe "PAYMENT_COMPLETED"
      response.paymentCompletedDateTime!!.shouldHaveSameDayAs(LocalDateTime.now())
    }

    "결제완료받았는데 금액이 다르면 오류 발생한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId

      // act & assert
      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount + 1000
      )
      val ex = shouldThrow<IllegalStateException> {
        paymentService.completePayment(completePaymentCommand)
      }
      ex shouldHaveMessage
          "payment amounts are different(initial = ${createPaymentRequestCommand.amount}, this time = ${completePaymentCommand.amount})"
    }


    "이미 결제완료됐는데 또 결제완료 받으면 오류 발생한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId

      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount
      )
      paymentService.completePayment(completePaymentCommand)

      // act & assert
      val ex = shouldThrow<IllegalStateException> {
        paymentService.completePayment(completePaymentCommand)
      }
      ex shouldHaveMessage "payment status is already completed"
    }

    "결제실패 상태에서 결제완료 받으면 결제완료로 변경한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId

      val failPaymentCommand = FailPaymentCommand(
        paymentId = paymentId,
        paymentFailedDateTime = LocalDateTime.now(),
        paymentFailedCauseMessage = "정지된 카드"
      )
      paymentService.failPayment(failPaymentCommand)

      // act
      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount
      )
      paymentService.completePayment(completePaymentCommand)

      // assert
      val response = paymentService.searchPaymentHistory(paymentId)
      response.paymentId shouldBe paymentId
      response.paymentStatus shouldBe "PAYMENT_COMPLETED"
      response.paymentCompletedDateTime!!.shouldHaveSameDayAs(LocalDateTime.now())
      response.paymentFailedDateTime shouldBe null
      response.paymentFailedCauseMessage shouldBe null
    }

    "결제완료 상태에서 결제실패 받으면 오류 발생한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId
      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount
      )
      paymentService.completePayment(completePaymentCommand)

      // act & assert
      val failPaymentCommand = FailPaymentCommand(
        paymentId = paymentId,
        paymentFailedDateTime = LocalDateTime.now(),
        paymentFailedCauseMessage = "정지된 카드"
      )
      val ex = shouldThrow<IllegalStateException> {
        paymentService.failPayment(failPaymentCommand)
      }
      ex shouldHaveMessage "payment status is already completed"
    }

    "결제실패되면 paymentStatus를 결제실패로 변경한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId

      // act
      val failPaymentCommand = FailPaymentCommand(
        paymentId = paymentId,
        paymentFailedDateTime = LocalDateTime.now(),
        paymentFailedCauseMessage = "정지된 카드"
      )
      paymentService.failPayment(failPaymentCommand)

      // assert
      val response = paymentService.searchPaymentHistory(paymentId)
      response.paymentId shouldBe paymentId
      response.paymentStatus shouldBe "PAYMENT_FAILED"
      response.paymentCompletedDateTime shouldBe null
      response.paymentFailedDateTime!!.shouldHaveSameDayAs(failPaymentCommand.paymentFailedDateTime)
      response.paymentFailedCauseMessage!! shouldContain "정지된 카드"
    }

    "없는 결제내역으로 결제완료 요청오면 오류 발생한다." {
      // arrange
      val paymentInformationId = paymentInformationService.registerPaymentInformation(
        createPaymentInformationRegisterCommand()
      )
      val createPaymentRequestCommand = createPaymentRequestCommand(paymentInformationId)
      val paymentId = paymentService.requestPayment(createPaymentRequestCommand).paymentId

      // act & assert
      val completePaymentCommand = CompletePaymentCommand(
        paymentId = paymentId + 1,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = createPaymentRequestCommand.amount
      )
      val ex = shouldThrow<IllegalStateException> {
        paymentService.completePayment(completePaymentCommand)
      }
    }

    "없는 결제정보로 결제요청하면 오류 발생한다." {
      val cmd = createPaymentRequestCommand(2)
      val ex = shouldThrow<IllegalArgumentException> {
        paymentService.requestPayment(cmd)
      }
      ex shouldHaveMessage "PaymentInformation(2) not found!"
    }
  }

}