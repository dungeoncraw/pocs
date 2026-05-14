package com.example.logistics.api

import com.example.logistics.domain.{TransportRate, TransportType}
import com.example.logistics.repository.TransportRateRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TransportRateControllerTest:

  private val transportRateRepository = mock(classOf[TransportRateRepository])
  private val objectMapper = {
    val mapper = ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper
  }
  private var mockMvc: MockMvc = _

  @BeforeEach
  def setUp(): Unit = {
    val controller = new TransportRateController(transportRateRepository)
    val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(messageConverter)
      .build()
  }

  @Test
  def testGetAllRates(): Unit = {
    val rates = List(
      TransportRate(TransportType.TRUCK, BigDecimal("50"), BigDecimal("0.5"), BigDecimal("0.1"), BigDecimal("10"), BigDecimal("100"), BigDecimal("1.2"), "USD"),
      TransportRate(TransportType.RAIL, BigDecimal("40"), BigDecimal("0.4"), BigDecimal("0.08"), BigDecimal("8"), BigDecimal("80"), BigDecimal("1.1"), "USD")
    )

    when(transportRateRepository.findAll()).thenReturn(rates)

    mockMvc.perform(get("/api/transport-rates"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.rates").isArray)
      .andExpect(jsonPath("$.rates.length()").value(2))
      .andExpect(jsonPath("$.rates[0].transportType").value("TRUCK"))
      .andExpect(jsonPath("$.rates[1].transportType").value("RAIL"))
  }
