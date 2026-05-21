package com.example.logistics.service

import com.example.logistics.domain.{Location, TransportType}
import scala.math.*

object DistanceCalculator:
  private val EarthRadiusKm = 6371.0

  def calculateDistance(origin: Location, destination: Location, transportType: TransportType): Double =
    val straightDistance = haversine(origin, destination)
    val multiplier = transportType match
      case TransportType.Truck => 1.25
      case TransportType.Rail  => 1.15
      case TransportType.Boat  => 1.35
      case TransportType.Motorcycle => 1.10
      case TransportType.Plane => 1.05
    
    straightDistance * multiplier

  private def haversine(loc1: Location, loc2: Location): Double =
    val dLat = (loc2.lat - loc1.lat).toRadians
    val dLon = (loc2.lon - loc1.lon).toRadians

    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(loc1.lat.toRadians) * cos(loc2.lat.toRadians) *
      sin(dLon / 2) * sin(dLon / 2)
    
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    EarthRadiusKm * c
