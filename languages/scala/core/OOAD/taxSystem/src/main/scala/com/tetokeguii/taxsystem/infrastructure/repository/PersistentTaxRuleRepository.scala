package com.tetokeguii.taxsystem.infrastructure.repository

import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.repository.TaxRuleRepository
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*
import scala.concurrent.{ExecutionContext, Future}

final class PersistentTaxRuleRepository(db: Database)(using ec: ExecutionContext) extends TaxRuleRepository {
  private class TaxRules(tag: Tag) extends Table[TaxRule](tag, "tax_rules") {
    def productId = column[String]("product_id")
    def state = column[String]("state")
    def year = column[Int]("year")
    def taxRate = column[BigDecimal]("tax_rate")
    def pk = primaryKey("pk_tax_rules", (productId, state, year))
    def * = (productId, state, year, taxRate) <> (TaxRule.apply.tupled, TaxRule.unapply)
  }

  private val taxRules = TableQuery[TaxRules]

  def createSchema(): Future[Unit] =
    db.run(taxRules.schema.createIfNotExists)

  override def findByProductStateYear(productId: String, state: String, year: Int): Future[Option[TaxRule]] =
    db.run(taxRules.filter(r => r.productId === productId && r.state === state && r.year === year).result.headOption)

  override def findByFilters(
      productId: Option[String],
      state: Option[String],
      year: Option[Int]
  ): Future[List[TaxRule]] = {
    val query = taxRules
      .filterOpt(productId)((r, id) => r.productId === id)
      .filterOpt(state)((r, s) => r.state === s)
      .filterOpt(year)((r, y) => r.year === y)
      .result
    db.run(query).map(_.toList)
  }

  override def create(taxRule: TaxRule): Future[TaxRule] =
    db.run(taxRules += taxRule).map(_ => taxRule)

  override def update(productId: String, state: String, year: Int, taxRate: BigDecimal): Future[Option[TaxRule]] = {
    val q = taxRules.filter(r => r.productId === productId && r.state === state && r.year === year)
    db.run(q.map(_.taxRate).update(taxRate)).flatMap {
      case 0 => Future.successful(None)
      case _ => findByProductStateYear(productId, state, year)
    }
  }

  override def delete(productId: String, state: String, year: Int): Future[Boolean] =
    db.run(taxRules.filter(r => r.productId === productId && r.state === state && r.year === year).delete).map(_ > 0)
}
