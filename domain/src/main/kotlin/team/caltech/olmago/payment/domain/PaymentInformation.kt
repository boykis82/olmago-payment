package team.caltech.olmago.payment.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class PaymentInformation(
  customerId: Long,
  cardNumber: String,
  cardCompany: CardCompany,
  createdDateTime: LocalDateTime
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id : Long? = null

  @Column(nullable = false, name = "cust_id")
  val customerId: Long = customerId

  @Column(nullable = false, name = "card_num", length = 16)
  val cardNumber: String = cardNumber

  @Column(nullable = false, name = "card_company")
  @Enumerated(EnumType.STRING)
  val cardCompany: CardCompany = cardCompany

  @Column(nullable = false, name = "created_at")
  val createdDateTime: LocalDateTime = createdDateTime

  @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "paymentInformation")
  protected val mutablePaymentInformationDetails: MutableList<PaymentInformationDetail> = mutableListOf()
  val paymentInformationDetails: List<PaymentInformationDetail> get() = mutablePaymentInformationDetails.toList()

  fun addPaymentInformationDetail(detail: PaymentInformationDetail) = mutablePaymentInformationDetails.add(detail)

  fun unlinkContract(contractId: Long, unlinkDateTime: LocalDateTime) {
    paymentInformationDetails.first { it.contractId == contractId }
      .unlinkContract(unlinkDateTime)
  }
}