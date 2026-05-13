package com.example.logistics.service

import com.example.logistics.domain.{Quote, TransportRate, TransportType}
import com.example.logistics.dto.*
import com.example.logistics.repository.{QuoteRepository, TransportRateRepository}
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class QuoteService(
  quoteRepository: QuoteRepository,
  transportRateRepository: TransportRateRepository,
  distanceService: DistanceService
):

  def createQuote(request: CreateQuoteRequest): QuoteResponse =
    val transportRate = getTransportRate(request.transportType)

    val straightDistanceKm = distanceService.calculateHaversineDistance(
      request.origin.latitude, request.origin.longitude,
      request.destination.latitude, request.destination.longitude
    )

    val distanceKm = distanceService.applyMultiplier(straightDistanceKm, transportRate.distanceMultiplier)

    val volumeM3 = (request.shipment.lengthCm * request.shipment.widthCm * request.shipment.heightCm) / BigDecimal(1000000)

    val basePrice = transportRate.basePrice
    val distanceCost = distanceKm * transportRate.pricePerKm
    val weightCost = request.shipment.weightKg * transportRate.pricePerKg
    val volumeCost = volumeM3 * transportRate.pricePerM3

    val calculatedPrice = basePrice + distanceCost + weightCost + volumeCost
    val finalPrice = calculatedPrice.max(transportRate.minimumPrice)

    val now = OffsetDateTime.now()
    val expiresAt = now.plusMinutes(30) // Example expiry

    val quote = Quote(
      id = UUID.randomUUID(),
      customerId = request.customerId,
      transportType = request.transportType,
      originLatitude = request.origin.latitude,
      originLongitude = request.origin.longitude,
      destinationLatitude = request.destination.latitude,
      destinationLongitude = request.destination.longitude,
      weightKg = request.shipment.weightKg,
      lengthCm = request.shipment.lengthCm,
      widthCm = request.shipment.widthCm,
      heightCm = request.shipment.heightCm,
      straightDistanceKm = straightDistanceKm,
      distanceKm = distanceKm,
      volumeM3 = volumeM3,
      basePrice = basePrice,
      distanceCost = distanceCost,
      weightCost = weightCost,
      volumeCost = volumeCost,
      minimumPrice = transportRate.minimumPrice,
      finalPrice = finalPrice,
      currency = transportRate.currency,
      createdAt = now,
      expiresAt = expiresAt
    )

    val savedQuote = quoteRepository.save(quote)
    mapToResponse(savedQuote, transportRate.distanceMultiplier)

  def getQuote(id: UUID): Option[QuoteResponse] =
    quoteRepository.findById(id).map { quote =>
      val transportRate = getTransportRate(quote.transportType)
      mapToResponse(quote, transportRate.distanceMultiplier)
    }

  private def getTransportRate(transportType: TransportType): TransportRate =
    transportRateRepository.findByTransportType(transportType)
      .getOrElse(throw new RuntimeException(s"Transport rate not found for $transportType"))

  private def mapToResponse(quote: Quote, multiplier: BigDecimal): QuoteResponse =
    QuoteResponse(
      quoteId = quote.id,
      customerId = quote.customerId,
      transportType = quote.transportType,
      origin = Location(quote.originLatitude, quote.originLongitude),
      destination = Location(quote.destinationLatitude, quote.destinationLongitude),
      shipment = ShipmentResponse(
        weightKg = quote.weightKg,
        lengthCm = quote.lengthCm,
        widthCm = quote.widthCm,
        heightCm = quote.heightCm,
        volumeM3 = quote.volumeM3
      ),
      distance = DistanceResponse(
        straightDistanceKm = quote.straightDistanceKm,
        distanceKm = quote.distanceKm,
        multiplier = multiplier
      ),
      price = PriceResponse(
        amount = quote.finalPrice,
        currency = quote.currency
      ),
      breakdown = BreakdownResponse(
        basePrice = quote.basePrice,
        distanceCost = quote.distanceCost,
        weightCost = quote.weightCost,
        volumeCost = quote.volumeCost,
        minimumPrice = quote.minimumPrice
      ),
      createdAt = quote.createdAt,
      expiresAt = quote.expiresAt
    )
