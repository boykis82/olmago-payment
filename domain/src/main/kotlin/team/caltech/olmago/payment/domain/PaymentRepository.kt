package team.caltech.olmago.payment.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PaymentRepository : JpaRepository<Payment, Long> {
  @Query(
    "SELECT P FROM Payment P JOIN FETCH P.mutablePaymentDetails PD WHERE P.id = :paymentId ORDER BY PD.contractId"
  )
  fun findPaymentWithDetails(@Param("paymentId") paymentId: Long): Payment?
}