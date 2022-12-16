package team.caltech.olmago.payment.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "rev_itm_cd")
class RevenueItem(
  revenueItemCode: String,
  revenueItemName: String,
  isIncludeVat: Boolean,
  vatRevenueItemCode: String
) {
  @Id
  @Column(name =  "rev_itm_cd", length = 7)
  val revenueItemCode: String = revenueItemCode

  @Column(name =  "rev_itm_nm", nullable = false, length = 80)
  val revenueItemName: String = revenueItemName

  @Column(name =  "vat_obj_yn", nullable = false)
  val isVatObject: Boolean = isIncludeVat

  @Column(name =  "vat_rev_itm_cd", nullable = true, length = 7)
  val vatRevenueItemCode: String = vatRevenueItemCode
}