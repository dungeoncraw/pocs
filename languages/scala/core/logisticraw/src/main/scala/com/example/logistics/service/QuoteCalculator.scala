package com.example.logistics.service

import com.example.logistics.domain.*
import com.example.logistics.provider.TransportRateProvider
import java.util.UUID

object QuoteCalculator:
  def calculate(request: QuoteRequest): Either[String, Quote] =
    val rate = TransportRateProvider.getRate(request.transportType)

    if (request.weightKg > rate.maxWeightKg) then
      Left(s"Weight ${request.weightKg}kg exceeds maximum capacity of ${rate.maxWeightKg}kg for ${request.transportType}")
    else if (request.dimensions.heightCm > rate.maxHeightCm) then
      Left(s"Height ${request.dimensions.heightCm}cm exceeds maximum capacity of ${rate.maxHeightCm}cm for ${request.transportType}")
    else if (request.dimensions.volumeM3 > rate.maxVolumeM3) then
      Left(s"Volume ${request.dimensions.volumeM3}m3 exceeds maximum capacity of ${rate.maxVolumeM3}m3 for ${request.transportType}")
    else
      val distance = DistanceCalculator.calculateDistance(request.origin, request.destination, request.transportType)
      val volume = request.dimensions.volumeM3

      val totalPrice = calculatePrice(
        basePrice = rate.basePrice,
        distanceKm = distance,
        pricePerKm = rate.pricePerKm,
        weightKg = request.weightKg,
        pricePerKg = rate.pricePerKg,
        volumeM3 = volume,
        pricePerM3 = rate.pricePerM3,
        minimumPrice = rate.minimumPrice
      )

      Right(Quote(
        id = UUID.randomUUID(),
        request = request,
        distanceKm = distance,
        totalPrice = totalPrice
      ))

  private def calculatePrice(
    basePrice: Double,
    distanceKm: Double,
    pricePerKm: Double,
    weightKg: Double,
    pricePerKg: Double,
    volumeM3: Double,
    pricePerM3: Double,
    minimumPrice: Double
  ): Double =
    val calculatedPrice = basePrice +
      (distanceKm * pricePerKm) +
      (weightKg * pricePerKg) +
      (volumeM3 * pricePerM3)

    math.max(calculatedPrice, minimumPrice)
