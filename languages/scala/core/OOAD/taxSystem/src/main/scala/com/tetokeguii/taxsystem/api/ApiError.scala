package com.tetokeguii.taxsystem.api

sealed trait ApiError

object ApiError {
  final case class TaxRuleNotFound(message: String) extends ApiError
  final case class ValidationError(message: String) extends ApiError
  final case class UnexpectedError(message: String) extends ApiError
}
