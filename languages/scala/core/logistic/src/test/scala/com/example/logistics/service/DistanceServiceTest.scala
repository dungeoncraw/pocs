package com.example.logistics.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

class DistanceServiceTest:
  private val distanceService = new DistanceService()

  @Test
  def testCalculateHaversineDistance(): Unit = {
    // New York: 40.7128° N, 74.0060° W
    // London: 51.5074° N, 0.1278° W
    val lat1 = BigDecimal("40.7128")
    val lon1 = BigDecimal("-74.0060")
    val lat2 = BigDecimal("51.5074")
    val lon2 = BigDecimal("-0.1278")

    val distance = distanceService.calculateHaversineDistance(lat1, lon1, lat2, lon2)

    // Distance between NY and London is approx 5570 km
    assertTrue(distance > 5500 && distance < 5650, s"Distance should be around 5570km, but was $distance")
  }

  @Test
  def testApplyMultiplier(): Unit = {
    val distance = BigDecimal("100")
    val multiplier = BigDecimal("1.2")
    val result = distanceService.applyMultiplier(distance, multiplier)
    assertEquals(BigDecimal("120.0"), result)
  }
