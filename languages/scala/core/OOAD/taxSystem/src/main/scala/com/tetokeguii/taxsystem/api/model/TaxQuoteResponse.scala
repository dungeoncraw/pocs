package com.tetokeguii.taxsystem.api.model

final case class TaxQuoteResponse(
    productId: String,
    state: String,
    year: Int,
    taxRate: BigDecimal
)
