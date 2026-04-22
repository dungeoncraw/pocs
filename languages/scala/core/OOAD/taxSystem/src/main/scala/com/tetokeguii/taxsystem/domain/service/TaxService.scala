package com.tetokeguii.taxsystem.domain.service

import com.tetokeguii.taxsystem.domain.model.TaxRule

trait TaxService {
  def getTaxQuote(productId: String, state: Option[String], year: Option[Int]): Either[String, List[TaxRule]]

  def listTaxRules(productId: Option[String], state: Option[String], year: Option[Int]): List[TaxRule]

  def createTaxRule(taxRule: TaxRule): Either[String, TaxRule]

  def updateTaxRule(productId: String, state: String, year: Int, taxRate: BigDecimal): Either[String, TaxRule]

  def deleteTaxRule(productId: String, state: String, year: Int): Either[String, Unit]
}
