package report

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.util.List

@Repository
class ReportDAO(private val entityManager: EntityManager):

  def findProductSales(): List[Array[AnyRef]] =
    entityManager.createQuery(
      "SELECT p.id, p.name, COUNT(o.id), SUM(o.amount), SUM(o.amount * p.price) " +
      "FROM Order o JOIN o.product p " +
      "GROUP BY p.id, p.name",
      classOf[Array[AnyRef]]
    ).getResultList
