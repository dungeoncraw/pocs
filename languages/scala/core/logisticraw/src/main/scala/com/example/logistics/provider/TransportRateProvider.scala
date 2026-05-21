package com.example.logistics.provider

import com.example.logistics.domain.{TransportRate, TransportType}

object TransportRateProvider:
  def getRate(transportType: TransportType): TransportRate = transportType match
    case TransportType.Truck => TransportRate(basePrice = 250, perKm = 0.75, perKg = 0.20, perM3 = 15, min = 400, maxWeight = 24000, maxHeight = 400, maxVolume = 80)
    case TransportType.Rail  => TransportRate(basePrice = 200, perKm = 0.55, perKg = 0.15, perM3 = 10, min = 350, maxWeight = 100000, maxHeight = 500, maxVolume = 150)
    case TransportType.Boat  => TransportRate(basePrice = 400, perKm = 0.40, perKg = 0.10, perM3 = 25, min = 600, maxWeight = 20000000, maxHeight = 1500, maxVolume = 5000)
    case TransportType.Motorcycle => TransportRate(basePrice = 100, perKm = 1.00, perKg = 0.30, perM3 = 5, min = 150, maxWeight = 150, maxHeight = 100, maxVolume = 0.5)
    case TransportType.Plane => TransportRate(basePrice = 500, perKm = 2.00, perKg = 0.50, perM3 = 10, min = 1000, maxWeight = 120000, maxHeight = 350, maxVolume = 400)
    case null => throw new IllegalArgumentException("Invalid transport type")

  private def TransportRate(basePrice: Double, perKm: Double, perKg: Double, perM3: Double, min: Double, maxWeight: Double, maxHeight: Double, maxVolume: Double): TransportRate =
    com.example.logistics.domain.TransportRate(basePrice, perKm, perKg, perM3, min, maxWeight, maxHeight, maxVolume)
