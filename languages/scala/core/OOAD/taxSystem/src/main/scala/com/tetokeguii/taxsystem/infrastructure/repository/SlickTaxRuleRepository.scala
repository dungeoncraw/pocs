package com.tetokeguii.taxsystem.infrastructure.repository

import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.repository.TaxRuleRepository

final class SlickTaxRuleRepository extends TaxRuleRepository {
  override def findByProductStateYear(productId: String, state: String, year: Int): Option[TaxRule] =
    ???

  override def findByFilters(
      productId: Option[String],
      state: Option[String],
      year: Option[Int]
  ): List[TaxRule] =
    ???

  override def create(taxRule: TaxRule): TaxRule =
    ???

  override def update(productId: String, state: String, year: Int, taxRate: BigDecimal): Option[TaxRule] =
    ???

  override def delete(productId: String, state: String, year: Int): Boolean =
    ???
}
