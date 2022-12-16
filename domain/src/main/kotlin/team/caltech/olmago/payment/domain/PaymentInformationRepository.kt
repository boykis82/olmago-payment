package team.caltech.olmago.payment.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PaymentInformationRepository : JpaRepository<PaymentInformation, Long> {
  @Query(
    "SELECT DISTINCT P " +
    "FROM PaymentInformation P JOIN FETCH P.mutablePaymentInformationDetails " +
    "WHERE P.customerId = :customerId " +
    "ORDER BY P.id DESC "
  )
  fun findByCustomerId(@Param("customerId") customerId: Long): List<PaymentInformation>

  @Query(
    "SELECT DISTINCT P " +
    "FROM PaymentInformation P JOIN FETCH P.mutablePaymentInformationDetails PD " +
    "WHERE P.id = :id "
  )
  fun findByIdWithValidContracts(@Param("id") id: Long): PaymentInformation?

  @Query(
    "SELECT DISTINCT P " +
    "FROM PaymentInformation P JOIN FETCH P.mutablePaymentInformationDetails PD " +
    "WHERE P.customerId = :customerId " +
    "AND PD.contractId = :contractId "
  )
  fun findByCustomerAndContractId(@Param("customerId") customerId: Long, @Param("contractId") contractId: Long): PaymentInformation?
}