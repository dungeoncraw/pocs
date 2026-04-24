package com.tetokeguii.taxsystem.api

import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import com.tetokeguii.taxsystem.api.model.*
import com.tetokeguii.taxsystem.domain.model.TaxRule
import com.tetokeguii.taxsystem.domain.service.TaxService
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

final class HttpTaxRoutes(val taxService: TaxService)(using ec: ExecutionContext) extends TaxRoutes with JsonSupport {

  val routes: Route =
    pathPrefix("tax") {
      concat(
        pathPrefix("quote" / Segment) { productId =>
          get {
            parameters("state".?, "year".as[Int].?) { (state, year) =>
              onComplete(getTaxQuote(productId, state, year)) {
                case Success(Right(quotes)) => complete(quotes)
                case Success(Left(err)) =>
                  val status = err match {
                    case ApiError.TaxRuleNotFound(_) => StatusCodes.NotFound
                    case ApiError.ValidationError(_) => StatusCodes.BadRequest
                    case _                           => StatusCodes.InternalServerError
                  }
                  complete(status, ErrorResponse(err.getClass.getSimpleName, err match {
                    case ApiError.TaxRuleNotFound(m) => m
                    case ApiError.ValidationError(m) => m
                    case ApiError.UnexpectedError(m) => m
                  }))
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError, ErrorResponse("InternalError", ex.getMessage))
              }
            }
          }
        },
        pathPrefix("rules") {
          concat(
            get {
              parameters("productId".?, "state".?, "year".as[Int].?) { (productId, state, year) =>
                complete(listTaxRules(productId, state, year))
              }
            },
            post {
              entity(as[CreateTaxRuleRequest]) { request =>
                onSuccess(createTaxRule(request)) {
                  case Right(rule) => complete(StatusCodes.Created, rule)
                  case Left(err)   => complete(StatusCodes.Conflict, ErrorResponse("Conflict", err match {
                    case ApiError.ValidationError(m) => m
                    case _ => "Error creating tax rule"
                  }))
                }
              }
            },
            path(Segment / Segment / IntNumber) { (productId, state, year) =>
              concat(
                put {
                  entity(as[UpdateTaxRuleRequest]) { request =>
                    onSuccess(updateTaxRule(productId, state, year, request)) {
                      case Right(rule) => complete(rule)
                      case Left(err) =>
                        val status = err match {
                          case ApiError.TaxRuleNotFound(_) => StatusCodes.NotFound
                          case _                           => StatusCodes.InternalServerError
                        }
                        complete(status, ErrorResponse("UpdateError", err match {
                          case ApiError.TaxRuleNotFound(m) => m
                          case _ => "Error updating tax rule"
                        }))
                    }
                  }
                },
                delete {
                  onSuccess(deleteTaxRule(productId, state, year)) {
                    case Right(_) => complete(StatusCodes.NoContent)
                    case Left(err) =>
                      val status = err match {
                        case ApiError.TaxRuleNotFound(_) => StatusCodes.NotFound
                        case _                           => StatusCodes.InternalServerError
                      }
                      complete(status, ErrorResponse("DeleteError", err match {
                        case ApiError.TaxRuleNotFound(m) => m
                        case _ => "Error deleting tax rule"
                      }))
                  }
                }
              )
            }
          )
        }
      )
    }

  override def getTaxQuote(
      productId: String,
      state: Option[String],
      year: Option[Int]
  ): Future[Either[ApiError, List[TaxQuoteResponse]]] = {
    taxService.getTaxQuote(productId, state, year).map {
      case Right(rules) =>
        Right(rules.map(r => TaxQuoteResponse(r.productId, r.state, r.year, r.taxRate)))
      case Left(msg) =>
        if (msg.contains("No tax rule found")) Left(ApiError.TaxRuleNotFound(msg))
        else Left(ApiError.ValidationError(msg))
    }
  }

  override def listTaxRules(
      productId: Option[String],
      state: Option[String],
      year: Option[Int]
  ): Future[List[TaxRule]] =
    taxService.listTaxRules(productId, state, year)

  override def createTaxRule(request: CreateTaxRuleRequest): Future[Either[ApiError, TaxRule]] = {
    val rule = TaxRule(request.productId, request.state, request.year, request.taxRate)
    taxService.createTaxRule(rule).map {
      case Right(r)  => Right(r)
      case Left(msg) => Left(ApiError.ValidationError(msg))
    }
  }

  override def updateTaxRule(
      productId: String,
      state: String,
      year: Int,
      request: UpdateTaxRuleRequest
  ): Future[Either[ApiError, TaxRule]] = {
    taxService.updateTaxRule(productId, state, year, request.taxRate).map {
      case Right(r)  => Right(r)
      case Left(msg) => Left(ApiError.TaxRuleNotFound(msg))
    }
  }

  override def deleteTaxRule(productId: String, state: String, year: Int): Future[Either[ApiError, Unit]] = {
    taxService.deleteTaxRule(productId, state, year).map {
      case Right(_)  => Right(())
      case Left(msg) => Left(ApiError.TaxRuleNotFound(msg))
    }
  }
}
