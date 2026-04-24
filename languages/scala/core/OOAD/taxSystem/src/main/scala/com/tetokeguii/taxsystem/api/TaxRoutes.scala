package com.tetokeguii.taxsystem.api

import com.tetokeguii.taxsystem.api.model.CreateTaxRuleRequest
import com.tetokeguii.taxsystem.api.model.TaxQuoteResponse
import com.tetokeguii.taxsystem.api.model.UpdateTaxRuleRequest
import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.service.TaxService
import scala.concurrent.Future

trait TaxRoutes {
  def taxService: TaxService

  def getTaxQuote(
      productId: String,
      state: Option[String],
      year: Option[Int]
  ): Future[Either[ApiError, List[TaxQuoteResponse]]]

  def listTaxRules(
      productId: Option[String],
      state: Option[String],
      year: Option[Int]
  ): Future[List[TaxRule]]

  def createTaxRule(request: CreateTaxRuleRequest): Future[Either[ApiError, TaxRule]]

  def updateTaxRule(
      productId: String,
      state: String,
      year: Int,
      request: UpdateTaxRuleRequest
  ): Future[Either[ApiError, TaxRule]]

  def deleteTaxRule(productId: String, state: String, year: Int): Future[Either[ApiError, Unit]]
}
