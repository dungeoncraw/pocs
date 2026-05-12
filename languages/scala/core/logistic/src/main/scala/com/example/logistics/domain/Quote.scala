package com.example.logistics.domain

import java.time.OffsetDateTime
import java.util.UUID

final case class Quote (
                       id: UUID,
                       customerId: String,
                       transportType: TransportType,
                       originLatitude: BigDecimal,
                       originLongitude: BigDecimal,
                       destinationLatitude: BigDecimal,
                       destinationLongitude: BigDecimal,

                       weightKg: BigDecimal,
                       lengthCm: BigDecimal,
                       widthCm: BigDecimal,
                       heightCm: BigDecimal,

                       straightDistanceKm: BigDecimal,
                       distanceKm: BigDecimal,
                       volumeM3: BigDecimal,

                       basePrice: BigDecimal,
                       distanceCost: BigDecimal,
                       weightCost: BigDecimal,
                       volumeCost: BigDecimal,
                       minimumPrice: BigDecimal,
                       finalPrice: BigDecimal,

                       currency: String,

                       createdAt: OffsetDateTime,
                       expiresAt: OffsetDateTime
                       )
