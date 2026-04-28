package com.tetokeguii.taxsystem.domain.service

import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.repository.TaxRuleRepository
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.time.LocalDate

class DomainTaxServiceSpec extends AnyWordSpec with Matchers with ScalaFutures {

  class MockTaxRuleRepository extends TaxRuleRepository {
    var findByProductStateYearFunc: (String, String, Int) => Future[Option[TaxRule]] = (_, _, _) => Future.successful(None)
    var findByFiltersFunc: (Option[String], Option[String], Option[Int]) => Future[List[TaxRule]] = (_, _, _) => Future.successful(Nil)
    var createFunc: TaxRule => Future[TaxRule] = r => Future.successful(r)
    var updateFunc: (String, String, Int, BigDecimal) => Future[Option[TaxRule]] = (_, _, _, _) => Future.successful(None)
    var deleteFunc: (String, String, Int) => Future[Boolean] = (_, _, _) => Future.successful(false)

    override def findByProductStateYear(productId: String, state: String, year: Int): Future[Option[TaxRule]] = findByProductStateYearFunc(productId, state, year)
    override def findByFilters(productId: Option[String], state: Option[String], year: Option[Int]): Future[List[TaxRule]] = findByFiltersFunc(productId, state, year)
    override def create(taxRule: TaxRule): Future[TaxRule] = createFunc(taxRule)
    override def update(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Option[TaxRule]] = updateFunc(productId, state, year, taxRate)
    override def delete(productId: String, state: String, year: Int): Future[Boolean] = deleteFunc(productId, state, year)
  }

  "DomainTaxService" should {

    "getTaxQuote" which {
      "returns a specific rule when product, state and year are provided" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rule = TaxRule("P1", "NY", 2024, 0.08)

        repository.findByProductStateYearFunc = (p, s, y) => {
          p shouldBe "P1"
          s shouldBe "NY"
          y shouldBe 2024
          Future.successful(Some(rule))
        }

        val result = service.getTaxQuote("P1", Some("NY"), Some(2024)).futureValue
        result shouldBe Right(List(rule))
      }

      "returns an error when specific rule is not found" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)

        repository.findByProductStateYearFunc = (_, _, _) => Future.successful(None)

        val result = service.getTaxQuote("P1", Some("NY"), Some(2024)).futureValue
        result shouldBe Left("No tax rule found for product P1 in NY for 2024")
      }

      "returns rules for the last 10 years when only state is provided" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val currentYear = LocalDate.now().getYear
        val rules = List(
          TaxRule("P1", "NY", currentYear, 0.08),
          TaxRule("P1", "NY", currentYear - 5, 0.07),
          TaxRule("P1", "NY", currentYear - 11, 0.06) // Should be filtered out
        )

        repository.findByFiltersFunc = (p, s, y) => {
          p shouldBe Some("P1")
          s shouldBe Some("NY")
          y shouldBe None
          Future.successful(rules)
        }

        val result = service.getTaxQuote("P1", Some("NY"), None).futureValue
        result match {
          case Right(list) =>
            list.size shouldBe 2
            list.map(_.year) shouldBe List(currentYear - 5, currentYear)
          case Left(err) => fail(s"Expected Right, got Left($err)")
        }
      }

      "returns rules for a specific year across all states" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rules = List(
          TaxRule("P1", "NY", 2024, 0.08),
          TaxRule("P1", "CA", 2024, 0.09)
        )

        repository.findByFiltersFunc = (p, s, y) => {
          p shouldBe Some("P1")
          s shouldBe None
          y shouldBe Some(2024)
          Future.successful(rules)
        }

        val result = service.getTaxQuote("P1", None, Some(2024)).futureValue
        result shouldBe Right(rules.sortBy(_.state))
      }

      "returns an error when neither state nor year is provided" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)

        val result = service.getTaxQuote("P1", None, None).futureValue
        result shouldBe Left("State or Year must be provided for a tax quote")
      }
    }

    "listTaxRules" should {
      "return all rules matching filters" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rules = List(TaxRule("P1", "NY", 2024, 0.08))

        repository.findByFiltersFunc = (p, s, y) => {
          p shouldBe Some("P1")
          Future.successful(rules)
        }

        val result = service.listTaxRules(Some("P1"), None, None).futureValue
        result shouldBe rules
      }
    }

    "createTaxRule" should {
      "successfully create a rule if it doesn't exist" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rule = TaxRule("P1", "NY", 2024, 0.08)

        repository.findByProductStateYearFunc = (_, _, _) => Future.successful(None)
        repository.createFunc = r => Future.successful(r)

        val result = service.createTaxRule(rule).futureValue
        result shouldBe Right(rule)
      }

      "return an error if the rule already exists" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rule = TaxRule("P1", "NY", 2024, 0.08)

        repository.findByProductStateYearFunc = (_, _, _) => Future.successful(Some(rule))

        val result = service.createTaxRule(rule).futureValue
        result shouldBe Left("Tax rule already exists for P1 in NY for 2024")
      }
    }

    "updateTaxRule" should {
      "successfully update an existing rule" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)
        val rule = TaxRule("P1", "NY", 2024, 0.09)

        repository.updateFunc = (p, s, y, r) => Future.successful(Some(rule))

        val result = service.updateTaxRule("P1", "NY", 2024, 0.09).futureValue
        result shouldBe Right(rule)
      }

      "return an error if the rule to update is not found" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)

        repository.updateFunc = (_, _, _, _) => Future.successful(None)

        val result = service.updateTaxRule("P1", "NY", 2024, 0.09).futureValue
        result shouldBe Left("Tax rule not found for P1 in NY for 2024")
      }
    }

    "deleteTaxRule" should {
      "successfully delete an existing rule" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)

        repository.deleteFunc = (_, _, _) => Future.successful(true)

        val result = service.deleteTaxRule("P1", "NY", 2024).futureValue
        result shouldBe Right(())
      }

      "return an error if the rule to delete is not found" in {
        val repository = new MockTaxRuleRepository
        val service = new DomainTaxService(repository)

        repository.deleteFunc = (_, _, _) => Future.successful(false)

        val result = service.deleteTaxRule("P1", "NY", 2024).futureValue
        result shouldBe Left("Tax rule not found for P1 in NY for 2024")
      }
    }
  }
}
