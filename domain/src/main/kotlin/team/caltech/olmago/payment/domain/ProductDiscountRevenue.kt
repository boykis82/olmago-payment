package team.caltech.olmago.payment.domain

import javax.persistence.*

@Entity
@Table(
  name = "prod_dc_rev",
  indexes = [
    Index(name =  "prod_dc_n1", columnList = "prod_cd, dc_cd, rev_itm_cd", unique = true)
  ]
)
class ProductDiscountRevenue(
  productCode: String,
  discountPolicyCode: String?,
  revenueItem: RevenueItem,
  separatedRevenueAmount: Long
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column(nullable = false, length = 10, name = "prod_cd")
  val productCode: String = productCode

  @Column(nullable = true, length = 10, name = "dc_cd")
  val discountPolicyCode: String? = discountPolicyCode

  @ManyToOne
  @JoinColumn(name = "rev_itm_cd")
  val revenueItem: RevenueItem = revenueItem

  @Column(nullable = false, name = "sep_rev_amt")
  val separatedRevenueAmount: Long = separatedRevenueAmount
}