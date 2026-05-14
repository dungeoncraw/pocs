package com.example.logistics.repository

import com.example.logistics.domain.{Quote, TransportType}
import org.junit.jupiter.api.{BeforeEach, Test}
import org.junit.jupiter.api.Assertions.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource

import java.time.OffsetDateTime
import java.util.UUID

class QuoteRepositoryTest:

  private var jdbcTemplate: JdbcTemplate = _
  private var repository: QuoteRepository = _

  @BeforeEach
  def setUp(): Unit = {
    val dataSource = DriverManagerDataSource()
    dataSource.setDriverClassName("org.h2.Driver")
    dataSource.setUrl("jdbc:h2:mem:quotes_test;DB_CLOSE_DELAY=-1")
    dataSource.setUsername("sa")
    dataSource.setPassword("")

    jdbcTemplate = JdbcTemplate(dataSource)
    repository = QuoteRepository(jdbcTemplate)

    jdbcTemplate.execute("DROP TABLE IF EXISTS quotes")
    jdbcTemplate.execute(
      """
        CREATE TABLE quotes (
          id UUID PRIMARY KEY,
          customer_id VARCHAR(100) NOT NULL,
          transport_type VARCHAR(20) NOT NULL,
          origin_latitude NUMERIC(10,8) NOT NULL,
          origin_longitude NUMERIC(11,8) NOT NULL,
          destination_latitude NUMERIC(10,8) NOT NULL,
          destination_longitude NUMERIC(11,8) NOT NULL,
          weight_kg NUMERIC(19,4) NOT NULL,
          length_cm NUMERIC(19,4) NOT NULL,
          width_cm NUMERIC(19,4) NOT NULL,
          height_cm NUMERIC(19,4) NOT NULL,
          straight_distance_km NUMERIC(19,4) NOT NULL,
          distance_km NUMERIC(19,4) NOT NULL,
          volume_m3 NUMERIC(19,4) NOT NULL,
          base_price NUMERIC(19,4) NOT NULL,
          distance_cost NUMERIC(19,4) NOT NULL,
          weight_cost NUMERIC(19,4) NOT NULL,
          volume_cost NUMERIC(19,4) NOT NULL,
          minimum_price NUMERIC(19,4) NOT NULL,
          final_price NUMERIC(19,4) NOT NULL,
          currency CHAR(3) NOT NULL,
          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
          expires_at TIMESTAMP WITH TIME ZONE NOT NULL
        )
      """
    )
  }

  @Test
  def testSaveAndFindById(): Unit = {
    val id = UUID.randomUUID()
    val now = OffsetDateTime.now()
    val quote = Quote(
      id = id,
      customerId = "customer-1",
      transportType = TransportType.TRUCK,
      originLatitude = BigDecimal("40.7128"),
      originLongitude = BigDecimal("-74.0060"),
      destinationLatitude = BigDecimal("34.0522"),
      destinationLongitude = BigDecimal("-118.2437"),
      weightKg = BigDecimal("100"),
      lengthCm = BigDecimal("100"),
      widthCm = BigDecimal("100"),
      heightCm = BigDecimal("100"),
      straightDistanceKm = BigDecimal("3935.74"),
      distanceKm = BigDecimal("4722.89"),
      volumeM3 = BigDecimal("1.0"),
      basePrice = BigDecimal("50.00"),
      distanceCost = BigDecimal("2361.45"),
      weightCost = BigDecimal("10.00"),
      volumeCost = BigDecimal("10.00"),
      minimumPrice = BigDecimal("100.00"),
      finalPrice = BigDecimal("2431.45"),
      currency = "USD",
      createdAt = now,
      expiresAt = now.plusMinutes(30)
    )

    repository.save(quote)

    val foundOpt = repository.findById(id)
    assertTrue(foundOpt.isDefined)
    val found = foundOpt.get
    assertEquals(id, found.id)
    assertEquals("customer-1", found.customerId)
    assertEquals(TransportType.TRUCK, found.transportType)
    assertEquals(0, BigDecimal("2431.45").compareTo(found.finalPrice))
  }

  @Test
  def testFindByIdNotFound(): Unit = {
    val result = repository.findById(UUID.randomUUID())
    assertTrue(result.isEmpty)
  }
