package team.caltech.olmago.payment.domain

import team.caltech.olmago.payment.domain.PaymentStatus.PAYMENT_COMPLETED
import team.caltech.olmago.payment.domain.PaymentStatus.PAYMENT_FAILED
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Payment(
  paymentRequestedDateTime: LocalDateTime,
  paymentStatus: PaymentStatus,
  amount: Long,
  paymentInformationId: Long
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id : Long? = null

  @Column(nullable = false, name = "pay_req_dtm")
  val paymentRequestedDateTime: LocalDateTime = paymentRequestedDateTime

  @Column(nullable = true, name = "pay_cmpl_dtm")
  var paymentCompletedDateTime: LocalDateTime? = null

  @Column(nullable = true, name = "pay_fail_dtm")
  var paymentFailedDateTime: LocalDateTime? = null

  @Column(nullable = true, name = "pay_fail_msg")
  var paymentFailedCauseMessage: String? = null

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "pay_status")
  var paymentStatus: PaymentStatus = paymentStatus

  @Column(nullable = false, name = "amt")
  val amount: Long = amount

  @Column(nullable = false, name = "pay_info_id")
  val paymentInformationId: Long = paymentInformationId

  @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "payment")
  protected val mutablePaymentDetails: MutableList<PaymentDetail> = mutableListOf()
  val paymentDetails: List<PaymentDetail> get() = mutablePaymentDetails.toList()

  fun addPaymentDetail(paymentDetail: PaymentDetail) =
    mutablePaymentDetails.add(paymentDetail)

  fun complete(paymentCompletedDateTime: LocalDateTime, amount: Long) {
    check(this.amount == amount) { "payment amounts are different(initial = ${this.amount}, this time = ${amount})" }
    check(paymentStatus != PAYMENT_COMPLETED) { "payment status is already completed" }
    this.paymentCompletedDateTime = paymentCompletedDateTime
    this.paymentStatus = PAYMENT_COMPLETED
    this.paymentFailedDateTime = null
    this.paymentFailedCauseMessage = null
  }

  fun fail(paymentFailedDateTime: LocalDateTime, paymentFailedCauseMessage: String) {
    check(paymentStatus != PAYMENT_COMPLETED) { "payment status is already completed" }
    this.paymentFailedDateTime = paymentFailedDateTime
    this.paymentStatus = PAYMENT_FAILED
    this.paymentFailedCauseMessage = paymentFailedCauseMessage
  }
}