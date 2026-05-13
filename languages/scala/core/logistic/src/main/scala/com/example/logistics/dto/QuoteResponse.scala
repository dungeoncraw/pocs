package com.example.logistics.dto

import com.example.logistics.domain.TransportType
import java.time.OffsetDateTime
import java.util.UUID

case class PriceResponse(
  amount: BigDecimal,
  currency: String
)

case class BreakdownResponse(
  basePrice: BigDecimal,
  distanceCost: BigDecimal,
  weightCost: BigDecimal,
  volumeCost: BigDecimal,
  minimumPrice: BigDecimal
)

case class DistanceResponse(
  straightDistanceKm: BigDecimal,
  distanceKm: BigDecimal,
  method: String = "HAVERSINE_WITH_MULTIPLIER",
  multiplier: BigDecimal
)

case class ShipmentResponse(
  weightKg: BigDecimal,
  lengthCm: BigDecimal,
  widthCm: BigDecimal,
  heightCm: BigDecimal,
  volumeM3: BigDecimal
)

case class QuoteResponse(
  quoteId: UUID,
  customerId: String,
  transportType: TransportType,
  origin: Location,
  destination: Location,
  shipment: ShipmentResponse,
  distance: DistanceResponse,
  price: PriceResponse,
  breakdown: BreakdownResponse,
  createdAt: OffsetDateTime,
  expiresAt: OffsetDateTime
)
