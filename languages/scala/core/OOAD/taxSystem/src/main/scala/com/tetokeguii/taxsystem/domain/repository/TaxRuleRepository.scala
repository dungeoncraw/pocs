package com.tetokeguii.taxsystem.domain.repository

import com.tetokeguii.taxsystem.domain.model.TaxRule

trait TaxRuleRepository {
  def findByProductStateYear(productId: String, state: String, year: Int): Option[TaxRule]

  def findByFilters(productId: Option[String], state: Option[String], year: Option[Int]): List[TaxRule]

  def create(taxRule: TaxRule): TaxRule

  def update(productId: String, state: String, year: Int, taxRate: BigDecimal): Option[TaxRule]

  def delete(productId: String, state: String, year: Int): Boolean
}
