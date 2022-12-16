package team.caltech.olmago.payment.service

import team.caltech.olmago.payment.service.dto.*
import team.caltech.olmago.payment.service.proxy.pg.PgPayCommand
import team.caltech.olmago.payment.service.proxy.pg.PgPayResponse
import java.time.LocalDateTime
import java.util.*

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
    LocalDateTime.of(2022,12,8,15,20,20)
  )

fun createPaymentRequestCommand(paymentInformationId: Long): PaymentRequestCommand =
  PaymentRequestCommand(paymentInformationId = paymentInformationId,4900,
    listOf(
      ContractPaymentInformation(1, 4900,
        listOf(
          ProductPaymentInformation("NMP0000001", 9900,
            listOf(DiscountPaymentInformation(discountPolicyCode = "DC00000001",-5000))
          ),
          ProductPaymentInformation("NMB0000001", 2000,
            listOf(DiscountPaymentInformation("DC00000002",-2000))
          ),
          ProductPaymentInformation("NMB0000002", 2000,
            listOf(DiscountPaymentInformation("DC00000002",-2000))
          )
        )
      ),
      ContractPaymentInformation(2,0,
        listOf(
          ProductPaymentInformation("NMO0000001",5000,
            listOf(DiscountPaymentInformation("DC00000003",-5000))
          )
        )
      )
    )
  )

fun createPgPayCommand(
  paymentId: Long,
  amount: Long,
  payRequestDateTime: LocalDateTime = LocalDateTime.now()
): PgPayCommand =
  PgPayCommand(
    cardNumber = "1234567887654321",
    payRequestDateTime = payRequestDateTime,
    payAmount = amount,
    paymentId = paymentId
  )

fun createPgPayResponse(paymentId: Long, isSuccess: Boolean, failedReasonMessage: String?) =
  PgPayResponse(
    paymentId = paymentId,
    pgPaymentId = UUID.randomUUID().toString(),
    isSuccess = isSuccess,
    failedReasonMessage = failedReasonMessage
  )