package com.tetokeguii.taxsystem.api.model

final case class TaxQuoteRequest(
    productId: String,
    state: Option[String],
    year: Option[Int]
)
