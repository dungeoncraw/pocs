package com.tetokeguii.taxsystem.api

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat, RootJsonWriter}
import com.tetokeguii.taxsystem.api.model.*
import com.tetokeguii.taxsystem.domain.model.TaxRule

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit def taxQuoteResponseFormat: RootJsonFormat[TaxQuoteResponse] = jsonFormat4(TaxQuoteResponse.apply)
  implicit def createTaxRuleRequestFormat: RootJsonFormat[CreateTaxRuleRequest] = jsonFormat4(CreateTaxRuleRequest.apply)
  implicit def updateTaxRuleRequestFormat: RootJsonFormat[UpdateTaxRuleRequest] = jsonFormat1(UpdateTaxRuleRequest.apply)
  implicit def taxRuleFormat: RootJsonFormat[TaxRule] = jsonFormat4(TaxRule.apply)
  implicit def errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse.apply)

  implicit def taxQuoteResponseListFormat: RootJsonFormat[List[TaxQuoteResponse]] = listFormat(using taxQuoteResponseFormat)
  implicit def taxRuleListFormat: RootJsonFormat[List[TaxRule]] = listFormat(using taxRuleFormat)
}
