package com.tetokeguii.taxsystem.domain.model

final case class TaxRule(
    productId: String,
    state: String,
    year: Int,
    taxRate: BigDecimal
)
