package team.caltech.olmago.payment.domain

import javax.persistence.*

@Entity
class PaymentDetail(
  payment: Payment,
  contractId: Long,
  amount: Long,
  revenueItemCode: String
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id : Long? = null

  @ManyToOne
  @JoinColumn(name = "pay_id")
  val payment: Payment = payment

  @Column(nullable = false, name = "contract_id")
  val contractId: Long = contractId

  @Column(nullable = false, name = "amt")
  val amount: Long = amount

  @Column(nullable = false, name = "rev_itm_cd")
  val revenueItemCode: String = revenueItemCode
}