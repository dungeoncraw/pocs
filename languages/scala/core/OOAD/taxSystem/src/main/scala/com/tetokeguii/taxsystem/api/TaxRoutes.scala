package com.tetokeguii.taxsystem.api

import com.tetokeguii.taxsystem.api.model.CreateTaxRuleRequest
import com.tetokeguii.taxsystem.api.model.TaxQuoteResponse
import com.tetokeguii.taxsystem.api.model.UpdateTaxRuleRequest
import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.service.TaxService

trait TaxRoutes {
  def taxService: TaxService

  def getTaxQuote(
      productId: String,
      state: Option[String],
      year: Option[Int]
  ): Either[ApiError, List[TaxQuoteResponse]]

  def listTaxRules(
      productId: Option[String],
      state: Option[String],
      year: Option[Int]
  ): List[TaxRule]

  def createTaxRule(request: CreateTaxRuleRequest): Either[ApiError, TaxRule]

  def updateTaxRule(
      productId: String,
      state: String,
      year: Int,
      request: UpdateTaxRuleRequest
  ): Either[ApiError, TaxRule]

  def deleteTaxRule(productId: String, state: String, year: Int): Either[ApiError, Unit]
}
