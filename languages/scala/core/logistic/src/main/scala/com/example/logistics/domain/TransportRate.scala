package com.example.logistics.domain

final case class TransportRate (
                               transportType: TransportType,
                               basePrice: BigDecimal,
                               pricePerKm: BigDecimal,
                               pricePerKg: BigDecimal,
                               pricePerM3: BigDecimal,
                               minimumPrice: BigDecimal,
                               distanceMultiplier: BigDecimal,
                               currency: String
                               )
