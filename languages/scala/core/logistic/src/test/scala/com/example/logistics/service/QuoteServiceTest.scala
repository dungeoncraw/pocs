package com.example.logistics.service

import com.example.logistics.domain.{Quote, TransportRate, TransportType}
import com.example.logistics.dto.*
import com.example.logistics.repository.{QuoteRepository, TransportRateRepository}
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

import java.util.UUID

class QuoteServiceTest:
  private val quoteRepository = mock(classOf[QuoteRepository])
  private val transportRateRepository = mock(classOf[TransportRateRepository])
  private val distanceService = mock(classOf[DistanceService])
  private val quoteService = new QuoteService(quoteRepository, transportRateRepository, distanceService)

  @Test
  def testCreateQuote(): Unit = {
    val request = CreateQuoteRequest(
      customerId = "customer-1",
      transportType = TransportType.TRUCK,
      origin = Location(BigDecimal("40.7128"), BigDecimal("-74.0060")),
      destination = Location(BigDecimal("34.0522"), BigDecimal("-118.2437")),
      shipment = Shipment(
        weightKg = BigDecimal("100"),
        lengthCm = BigDecimal("100"),
        widthCm = BigDecimal("100"),
        heightCm = BigDecimal("100")
      )
    )

    val transportRate = TransportRate(
      transportType = TransportType.TRUCK,
      basePrice = BigDecimal("50.00"),
      pricePerKm = BigDecimal("0.50"),
      pricePerKg = BigDecimal("0.10"),
      pricePerM3 = BigDecimal("10.00"),
      minimumPrice = BigDecimal("100.00"),
      distanceMultiplier = BigDecimal("1.2"),
      currency = "USD"
    )

    when(transportRateRepository.findByTransportType(TransportType.TRUCK)).thenReturn(Some(transportRate))
    when(distanceService.calculateHaversineDistance(any(), any(), any(), any())).thenReturn(BigDecimal("3935.74"))
    when(distanceService.applyMultiplier(any(), any())).thenReturn(BigDecimal("4722.89"))
    when(quoteRepository.save(any[Quote])).thenAnswer(invocation => invocation.getArgument(0))

    val response = quoteService.createQuote(request)

    assertNotNull(response)
    assertEquals("customer-1", response.customerId)
    assertEquals(TransportType.TRUCK, response.transportType)
    assertEquals(BigDecimal("1.0"), response.shipment.volumeM3) // (100*100*100)/1000000 = 1.0

    // Calculations:
    // basePrice = 50.00
    // distanceCost = 4722.89 * 0.50 = 2361.445
    // weightCost = 100 * 0.10 = 10.00
    // volumeCost = 1.0 * 10.00 = 10.00
    // calculatedPrice = 50 + 2361.445 + 10 + 10 = 2431.445
    // finalPrice = calculatedPrice.max(100) = 2431.445
    
    assertEquals(BigDecimal("2431.445"), response.price.amount)
    verify(quoteRepository).save(any[Quote])
  }

  @Test
  def testGetQuoteNotFound(): Unit = {
    val id = UUID.randomUUID()
    when(quoteRepository.findById(id)).thenReturn(None)
    val result = quoteService.getQuote(id)
    assertTrue(result.isEmpty)
  }
