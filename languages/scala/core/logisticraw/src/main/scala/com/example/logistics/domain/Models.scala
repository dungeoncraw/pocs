package com.example.logistics.domain

import java.util.UUID

case class Location(lat: Double, lon: Double)

case class Dimensions(lengthCm: Double, widthCm: Double, heightCm: Double):
  def volumeM3: Double = (lengthCm * widthCm * heightCm) / 1_000_000.0

case class QuoteRequest(
  origin: Location,
  destination: Location,
  weightKg: Double,
  dimensions: Dimensions,
  transportType: TransportType
)

case class TransportRate(
  basePrice: Double,
  pricePerKm: Double,
  pricePerKg: Double,
  pricePerM3: Double,
  minimumPrice: Double,
  maxWeightKg: Double,
  maxHeightCm: Double,
  maxVolumeM3: Double
)

case class Quote(
  id: UUID,
  request: QuoteRequest,
  distanceKm: Double,
  totalPrice: Double
)
