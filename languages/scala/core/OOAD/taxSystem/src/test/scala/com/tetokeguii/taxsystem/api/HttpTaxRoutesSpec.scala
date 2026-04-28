package com.tetokeguii.taxsystem.api

import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import com.tetokeguii.taxsystem.api.model.*
import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.service.TaxService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future

class HttpTaxRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  class MockTaxService extends TaxService {
    var getTaxQuoteFunc: (String, Option[String], Option[Int]) => Future[Either[String, List[TaxRule]]] = (_, _, _) => Future.successful(Right(Nil))
    var listTaxRulesFunc: (Option[String], Option[String], Option[Int]) => Future[List[TaxRule]] = (_, _, _) => Future.successful(Nil)
    var createTaxRuleFunc: TaxRule => Future[Either[String, TaxRule]] = r => Future.successful(Right(r))
    var updateTaxRuleFunc: (String, String, Int, BigDecimal) => Future[Either[String, TaxRule]] = (p, s, y, r) => Future.successful(Right(TaxRule(p, s, y, r)))
    var deleteTaxRuleFunc: (String, String, Int) => Future[Either[String, Unit]] = (_, _, _) => Future.successful(Right(()))

    override def getTaxQuote(productId: String, state: Option[String], year: Option[Int]): Future[Either[String, List[TaxRule]]] = getTaxQuoteFunc(productId, state, year)
    override def listTaxRules(productId: Option[String], state: Option[String], year: Option[Int]): Future[List[TaxRule]] = listTaxRulesFunc(productId, state, year)
    override def createTaxRule(taxRule: TaxRule): Future[Either[String, TaxRule]] = createTaxRuleFunc(taxRule)
    override def updateTaxRule(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Either[String, TaxRule]] = updateTaxRuleFunc(productId, state, year, taxRate)
    override def deleteTaxRule(productId: String, state: String, year: Int): Future[Either[String, Unit]] = deleteTaxRuleFunc(productId, state, year)
  }

  val mockService = new MockTaxService
  val routes = new HttpTaxRoutes(mockService).routes

  "HttpTaxRoutes" should {

    "GET /tax/quote/{productId}" should {
      "return 200 and quotes when found" in {
        val rule = TaxRule("P1", "NY", 2024, 0.08)
        mockService.getTaxQuoteFunc = (p, s, y) => {
          p shouldBe "P1"
          s shouldBe Some("NY")
          y shouldBe Some(2024)
          Future.successful(Right(List(rule)))
        }

        Get("/tax/quote/P1?state=NY&year=2024") ~> routes ~> check {
          status shouldBe StatusCodes.OK
          entityAs[List[TaxQuoteResponse]] shouldBe List(TaxQuoteResponse("P1", "NY", 2024, 0.08))
        }
      }

      "return 404 when no tax rule found" in {
        mockService.getTaxQuoteFunc = (_, _, _) => Future.successful(Left("No tax rule found for product P1 in NY for 2024"))

        Get("/tax/quote/P1?state=NY&year=2024") ~> routes ~> check {
          status shouldBe StatusCodes.NotFound
          entityAs[ErrorResponse].message should include("No tax rule found")
        }
      }

      "return 400 when validation fails" in {
        mockService.getTaxQuoteFunc = (_, _, _) => Future.successful(Left("State or Year must be provided"))

        Get("/tax/quote/P1") ~> routes ~> check {
          status shouldBe StatusCodes.BadRequest
          entityAs[ErrorResponse].message should include("State or Year must be provided")
        }
      }
    }

    "GET /tax/rules" should {
      "return all matching rules" in {
        val rules = List(TaxRule("P1", "NY", 2024, 0.08))
        mockService.listTaxRulesFunc = (p, s, y) => {
          p shouldBe Some("P1")
          Future.successful(rules)
        }

        Get("/tax/rules?productId=P1") ~> routes ~> check {
          status shouldBe StatusCodes.OK
          entityAs[List[TaxRule]] shouldBe rules
        }
      }
    }

    "POST /tax/rules" should {
      "return 201 when rule is created" in {
        val request = CreateTaxRuleRequest("P1", "NY", 2024, 0.08)
        val rule = TaxRule("P1", "NY", 2024, 0.08)
        mockService.createTaxRuleFunc = r => {
          r shouldBe rule
          Future.successful(Right(rule))
        }

        Post("/tax/rules", request) ~> routes ~> check {
          status shouldBe StatusCodes.Created
          entityAs[TaxRule] shouldBe rule
        }
      }

      "return 409 when conflict occurs" in {
        val request = CreateTaxRuleRequest("P1", "NY", 2024, 0.08)
        mockService.createTaxRuleFunc = _ => Future.successful(Left("Tax rule already exists"))

        Post("/tax/rules", request) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          entityAs[ErrorResponse].message shouldBe "Tax rule already exists"
        }
      }
    }

    "PUT /tax/rules/{productId}/{state}/{year}" should {
      "return 200 when rule is updated" in {
        val request = UpdateTaxRuleRequest(0.09)
        val rule = TaxRule("P1", "NY", 2024, 0.09)
        mockService.updateTaxRuleFunc = (p, s, y, r) => {
          p shouldBe "P1"
          s shouldBe "NY"
          y shouldBe 2024
          r shouldBe 0.09
          Future.successful(Right(rule))
        }

        Put("/tax/rules/P1/NY/2024", request) ~> routes ~> check {
          status shouldBe StatusCodes.OK
          entityAs[TaxRule] shouldBe rule
        }
      }

      "return 404 when rule not found for update" in {
        val request = UpdateTaxRuleRequest(0.09)
        mockService.updateTaxRuleFunc = (_, _, _, _) => Future.successful(Left("Tax rule not found"))

        Put("/tax/rules/P1/NY/2024", request) ~> routes ~> check {
          status shouldBe StatusCodes.NotFound
          entityAs[ErrorResponse].message shouldBe "Tax rule not found"
        }
      }
    }

    "DELETE /tax/rules/{productId}/{state}/{year}" should {
      "return 204 when rule is deleted" in {
        mockService.deleteTaxRuleFunc = (p, s, y) => {
          p shouldBe "P1"
          s shouldBe "NY"
          y shouldBe 2024
          Future.successful(Right(()))
        }

        Delete("/tax/rules/P1/NY/2024") ~> routes ~> check {
          status shouldBe StatusCodes.NoContent
        }
      }

      "return 404 when rule not found for deletion" in {
        mockService.deleteTaxRuleFunc = (_, _, _) => Future.successful(Left("Tax rule not found"))

        Delete("/tax/rules/P1/NY/2024") ~> routes ~> check {
          status shouldBe StatusCodes.NotFound
          entityAs[ErrorResponse].message shouldBe "Tax rule not found"
        }
      }
    }
  }
}
