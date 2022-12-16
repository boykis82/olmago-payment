package team.caltech.olmago.payment.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class PaymentInformationDetail(
  contractId: Long,
  productName: String,
  paymentInformation: PaymentInformation,
  createdDateTime: LocalDateTime
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column(nullable = false, name = "contract_id")
  val contractId: Long = contractId

  @ManyToOne
  @JoinColumn(name = "pay_info_id")
  val paymentInformation: PaymentInformation = paymentInformation

  @Column(nullable = false, name = "prod_nm")
  val productName: String = productName

  @Column(nullable = false, name = "sta_dtm")
  val startDateTime: LocalDateTime = createdDateTime

  @Column(nullable = true, name = "end_dtm")
  var endDateTime: LocalDateTime? = null

  fun unlinkContract(unlinkDateTime: LocalDateTime) {
    endDateTime = unlinkDateTime
  }
}