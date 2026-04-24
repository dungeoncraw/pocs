package com.tetokeguii.taxsystem.domain.repository

import com.tetokeguii.taxsystem.domain.model.TaxRule
import scala.concurrent.Future

trait TaxRuleRepository {
  def findByProductStateYear(productId: String, state: String, year: Int): Future[Option[TaxRule]]

  def findByFilters(productId: Option[String], state: Option[String], year: Option[Int]): Future[List[TaxRule]]

  def create(taxRule: TaxRule): Future[TaxRule]

  def update(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Option[TaxRule]]

  def delete(productId: String, state: String, year: Int): Future[Boolean]
}
