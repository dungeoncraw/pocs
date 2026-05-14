package com.example.logistics.api

import com.example.logistics.domain.TransportType
import com.example.logistics.dto.*
import com.example.logistics.service.QuoteService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.time.OffsetDateTime
import java.util.UUID

class QuoteControllerTest:

  private val quoteService = mock(classOf[QuoteService])
  private val objectMapper = {
    val mapper = ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.findAndRegisterModules()
    mapper
  }
  
  private var mockMvc: MockMvc = _

  @BeforeEach
  def setUp(): Unit = {
    val controller = new QuoteController(quoteService)
    val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(messageConverter)
      .build()
  }

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

    val quoteId = UUID.randomUUID()
    // Using null for dates to avoid Jackson serialization issues in test environment
    val response = QuoteResponse(
      quoteId = quoteId,
      customerId = "customer-1",
      transportType = TransportType.TRUCK,
      origin = request.origin,
      destination = request.destination,
      shipment = ShipmentResponse(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("1.0")),
      distance = DistanceResponse(BigDecimal("3935.74"), BigDecimal("4722.89"), multiplier = BigDecimal("1.2")),
      price = PriceResponse(BigDecimal("2431.45"), "USD"),
      breakdown = BreakdownResponse(BigDecimal("50.00"), BigDecimal("2361.45"), BigDecimal("10.00"), BigDecimal("10.00"), BigDecimal("100.00")),
      createdAt = null,
      expiresAt = null
    )

    when(quoteService.createQuote(any[CreateQuoteRequest])).thenReturn(response)

    mockMvc.perform(post("/api/quotes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.customerId").value("customer-1"))
  }

  @Test
  def testGetQuote(): Unit = {
    val quoteId = UUID.randomUUID()
    // Using null for dates to avoid Jackson serialization issues in test environment
    val response = QuoteResponse(
      quoteId = quoteId,
      customerId = "customer-1",
      transportType = TransportType.TRUCK,
      origin = Location(BigDecimal("40.7128"), BigDecimal("-74.0060")),
      destination = Location(BigDecimal("34.0522"), BigDecimal("-118.2437")),
      shipment = ShipmentResponse(BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("100"), BigDecimal("1.0")),
      distance = DistanceResponse(BigDecimal("3935.74"), BigDecimal("4722.89"), multiplier = BigDecimal("1.2")),
      price = PriceResponse(BigDecimal("2431.45"), "USD"),
      breakdown = BreakdownResponse(BigDecimal("50.00"), BigDecimal("2361.45"), BigDecimal("10.00"), BigDecimal("10.00"), BigDecimal("100.00")),
      createdAt = null,
      expiresAt = null
    )

    when(quoteService.getQuote(quoteId)).thenReturn(Some(response))

    mockMvc.perform(get(s"/api/quotes/$quoteId"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.customerId").value("customer-1"))
  }

  @Test
  def testGetQuoteNotFound(): Unit = {
    val quoteId = UUID.randomUUID()
    when(quoteService.getQuote(quoteId)).thenReturn(None)

    mockMvc.perform(get(s"/api/quotes/$quoteId"))
      .andExpect(status().isNotFound)
  }
