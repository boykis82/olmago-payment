package team.caltech.olmago.payment.service

import team.caltech.olmago.payment.service.dto.*
import java.time.LocalDateTime
import java.util.*

class TestSpecificLanguage {
  companion object {
    fun createPaymentInformationRegisterCommand() =
      PaymentInformationRegisterCommand(
        customerId = 1L,
        contracts = listOf(
          Contract(1L, "우주패스All"),
          Contract(2L, "배달의민족")
        ),
        cardNumber = "1234567887654321",
        cardCompanyCode = "SH"
      )

    fun createPaymentInformationRegisterCommands() =
      listOf(
        createPaymentInformationRegisterCommand(),
        PaymentInformationRegisterCommand(
          customerId = 1L,
          contracts = listOf(
            Contract(3L, "Flo")
          ),
          cardNumber = "1234567887651234",
          cardCompanyCode = "KB"
        ),
        PaymentInformationRegisterCommand(
          customerId = 2L,
          contracts = listOf(
            Contract(4L, "우주패스Mini")
          ),
          cardNumber = "1234567887659999",
          cardCompanyCode = "HN"
        )
      )

    fun createPaymentInformationResponse() =
      PaymentInformationResponse(
        1,
        1,
        "123456788765****",
        "SH",
        "신한카드",
        "우주패스All,배달의민족,",
        LocalDateTime.of(2022, 12, 8, 15, 20, 20)
      )

    fun createPaymentRequestCommand(paymentInformationId: Long): PaymentRequestCommand =
      PaymentRequestCommand(
        paymentInformationId = paymentInformationId, 4900,
        listOf(
          ContractPaymentInformation(
            1, 4900,
            listOf(
              ProductPaymentInformation(
                "NMP0000001", 9900,
                listOf(DiscountPaymentInformation(discountPolicyCode = "DC00000001", -5000))
              ),
              ProductPaymentInformation(
                "NMB0000001", 2000,
                listOf(DiscountPaymentInformation("DC00000002", -2000))
              ),
              ProductPaymentInformation(
                "NMB0000002", 2000,
                listOf(DiscountPaymentInformation("DC00000002", -2000))
              )
            )
          ),
          ContractPaymentInformation(
            2, 0,
            listOf(
              ProductPaymentInformation(
                "NMO0000001", 5000,
                listOf(DiscountPaymentInformation("DC00000003", -5000))
              )
            )
          )
        )
      )

    fun createPaymentCompleteCommand(paymentId: Long, amount: Long) =
      PaymentCompleteCommand(
        paymentId = paymentId,
        paymentCompletedDateTime = LocalDateTime.now(),
        amount = amount,
      )

    fun createPaymentFailedCommand(paymentId: Long) =
      PaymentFailCommand(
        paymentId = paymentId,
        paymentFailedDateTime = LocalDateTime.now(),
        paymentFailedCauseMessage = "..."
      )

    fun createDetailPaymentHistoryResponse(paymentId: Long) =
      DetailPaymentHistoryResponse(
        paymentId = paymentId,
        paymentInformationId = 1,
        paymentRequestDateTime = LocalDateTime.now(),
        paymentCompletedDateTime = null,
        paymentFailedDateTime = null,
        paymentFailedCauseMessage = null,
        paymentAmount = 1000,
        paymentStatus = "PAYMENT_AWAITING",
        paymentHistoryPerContracts = listOf(
          PaymentHistoryPerContractResponse(1, 1000),
          PaymentHistoryPerContractResponse(2, 2000)
        )
      )

    fun createSummaryPaymentHistory(paymentId: Long) =
      SummaryPaymentHistoryResponse(
        paymentId = paymentId,
        paymentInformationId = 1,
        paymentRequestDateTime = LocalDateTime.now(),
        paymentCompletedDateTime = null,
        paymentFailedDateTime = null,
        paymentFailedCauseMessage = null,
        paymentAmount = 1000,
        paymentStatus = "PAYMENT_AWAITING",
      )

    fun createSummaryPaymentHistoriesResponse(times: Long): List<SummaryPaymentHistoryResponse> {
      val response = ArrayList<SummaryPaymentHistoryResponse>()
      for (i in 1..times) {
        response.add(createSummaryPaymentHistory(i))
      }
      return response
    }
  }
}

