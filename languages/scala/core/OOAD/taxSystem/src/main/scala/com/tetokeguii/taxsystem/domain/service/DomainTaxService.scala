package com.tetokeguii.taxsystem.domain.service

import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.repository.TaxRuleRepository
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate

final class DomainTaxService(repository: TaxRuleRepository)(using ec: ExecutionContext) extends TaxService {

  override def getTaxQuote(productId: String, state: Option[String], year: Option[Int]): Future[Either[String, List[TaxRule]]] = {
    (state, year) match {
      case (Some(s), Some(y)) =>
        repository.findByProductStateYear(productId, s, y).map {
          case Some(rule) => Right(List(rule))
          case None       => Left(s"No tax rule found for product $productId in $s for $y")
        }

      case (Some(s), None) =>
        val currentYear = LocalDate.now().getYear
        val startYear = currentYear - 9
        repository.findByFilters(Some(productId), Some(s), None).map { rules =>
          val filtered = rules.filter(r => r.year >= startYear && r.year <= currentYear).sortBy(_.year)
          if (filtered.isEmpty) Left(s"No tax rules found for product $productId in $s for the last 10 years")
          else Right(filtered)
        }

      case (None, Some(y)) =>
        repository.findByFilters(Some(productId), None, Some(y)).map { rules =>
          if (rules.isEmpty) Left(s"No tax rules found for product $productId for year $y")
          else Right(rules.sortBy(_.state))
        }

      case (None, None) =>
        Future.successful(Left("State or Year must be provided for a tax quote"))
    }
  }

  override def listTaxRules(productId: Option[String], state: Option[String], year: Option[Int]): Future[List[TaxRule]] =
    repository.findByFilters(productId, state, year)

  override def createTaxRule(taxRule: TaxRule): Future[Either[String, TaxRule]] = {
    repository.findByProductStateYear(taxRule.productId, taxRule.state, taxRule.year).flatMap {
      case Some(_) => Future.successful(Left(s"Tax rule already exists for ${taxRule.productId} in ${taxRule.state} for ${taxRule.year}"))
      case None    => repository.create(taxRule).map(Right(_))
    }
  }

  override def updateTaxRule(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Either[String, TaxRule]] = {
    repository.update(productId, state, year, taxRate).map {
      case Some(rule) => Right(rule)
      case None       => Left(s"Tax rule not found for $productId in $state for $year")
    }
  }

  override def deleteTaxRule(productId: String, state: String, year: Int): Future[Either[String, Unit]] = {
    repository.delete(productId, state, year).map {
      case true  => Right(())
      case false => Left(s"Tax rule not found for $productId in $state for $year")
    }
  }
}
