package com.example.logistics.service

import org.springframework.stereotype.Service
import scala.math.*

@Service
class DistanceService:
  private val EarthRadiusKm = 6371.0

  def calculateHaversineDistance(lat1: BigDecimal, lon1: BigDecimal, lat2: BigDecimal, lon2: BigDecimal): BigDecimal =
    val dLat = toRadians(lat2.toDouble - lat1.toDouble)
    val dLon = toRadians(lon2.toDouble - lon1.toDouble)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(toRadians(lat1.toDouble)) * cos(toRadians(lat2.toDouble)) *
        sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    BigDecimal(EarthRadiusKm * c)

  def applyMultiplier(distance: BigDecimal, multiplier: BigDecimal): BigDecimal =
    distance * multiplier
