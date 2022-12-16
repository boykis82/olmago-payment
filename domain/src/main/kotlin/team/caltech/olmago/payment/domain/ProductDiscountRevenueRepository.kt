package team.caltech.olmago.payment.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductDiscountRevenueRepository :
  JpaRepository<ProductDiscountRevenue, Long> {

  fun findByProductCodeAndDiscountPolicyCode(productCode: String, discountPolicyCode: String?): List<ProductDiscountRevenue>
}