package com.example.logistics.dto

import com.example.logistics.domain.TransportType

case class Location(
  latitude: BigDecimal,
  longitude: BigDecimal
)

case class Shipment(
  weightKg: BigDecimal,
  lengthCm: BigDecimal,
  widthCm: BigDecimal,
  heightCm: BigDecimal
)

case class CreateQuoteRequest(
  customerId: String,
  transportType: TransportType,
  origin: Location,
  destination: Location,
  shipment: Shipment
)
