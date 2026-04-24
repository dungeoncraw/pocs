package com.tetokeguii.taxsystem.domain.service

import com.tetokeguii.taxsystem.domain.model.TaxRule
import scala.concurrent.Future

trait TaxService {
  def getTaxQuote(productId: String, state: Option[String], year: Option[Int]): Future[Either[String, List[TaxRule]]]

  def listTaxRules(productId: Option[String], state: Option[String], year: Option[Int]): Future[List[TaxRule]]

  def createTaxRule(taxRule: TaxRule): Future[Either[String, TaxRule]]

  def updateTaxRule(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Either[String, TaxRule]]

  def deleteTaxRule(productId: String, state: String, year: Int): Future[Either[String, Unit]]
}
