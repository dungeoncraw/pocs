package com.tetokeguii.taxsystem.api.model

final case class CreateTaxRuleRequest(
    productId: String,
    state: String,
    year: Int,
    taxRate: BigDecimal
)
