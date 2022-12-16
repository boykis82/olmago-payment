package team.caltech.olmago.payment.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RevenueRepository : JpaRepository<RevenueItem, String> {
}