package com.example.logistics.repository

import com.example.logistics.domain.TransportType
import org.junit.jupiter.api.{BeforeEach, Test}
import org.junit.jupiter.api.Assertions.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource

import java.math.BigDecimal

class TransportRateRepositoryTest:

  private var jdbcTemplate: JdbcTemplate = _
  private var repository: TransportRateRepository = _

  @BeforeEach
  def setUp(): Unit =
    val dataSource = DriverManagerDataSource()
    dataSource.setDriverClassName("org.h2.Driver")
    dataSource.setUrl("jdbc:h2:mem:transport_rates_test;DB_CLOSE_DELAY=-1")
    dataSource.setUsername("sa")
    dataSource.setPassword("")

    jdbcTemplate = JdbcTemplate(dataSource)
    repository = TransportRateRepository(jdbcTemplate)

    jdbcTemplate.execute("DROP TABLE IF EXISTS transport_rates")

    jdbcTemplate.execute(
      """
        CREATE TABLE transport_rates (
          transport_type VARCHAR(20) PRIMARY KEY,
          base_price NUMERIC(19,4) NOT NULL,
          price_per_km NUMERIC(19,4) NOT NULL,
          price_per_kg NUMERIC(19,4) NOT NULL,
          price_per_m3 NUMERIC(19,4) NOT NULL,
          minimum_price NUMERIC(19,4) NOT NULL,
          distance_multiplier NUMERIC(10,4) NOT NULL,
          currency CHAR(3) NOT NULL
        )
      """
    )

    jdbcTemplate.update(
      """
        INSERT INTO transport_rates (
          transport_type,
          base_price,
          price_per_km,
          price_per_kg,
          price_per_m3,
          minimum_price,
          distance_multiplier,
          currency
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      """,
      "TRUCK",
      BigDecimal("250.00"),
      BigDecimal("0.75"),
      BigDecimal("0.20"),
      BigDecimal("15.00"),
      BigDecimal("400.00"),
      BigDecimal("1.25"),
      "USD"
    )

    jdbcTemplate.update(
      """
        INSERT INTO transport_rates (
          transport_type,
          base_price,
          price_per_km,
          price_per_kg,
          price_per_m3,
          minimum_price,
          distance_multiplier,
          currency
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      """,
      "RAIL",
      BigDecimal("200.00"),
      BigDecimal("0.55"),
      BigDecimal("0.15"),
      BigDecimal("10.00"),
      BigDecimal("350.00"),
      BigDecimal("1.15"),
      "USD"
    )

  @Test
  def findByTransportTypeReturnsRateWhenFound(): Unit =
    val result = repository.findByTransportType(TransportType.TRUCK)

    assertTrue(result.isDefined)

    val rate = result.get

    assertEquals(TransportType.TRUCK, rate.transportType)
    assertEquals(0, BigDecimal("250.00").compareTo(rate.basePrice))
    assertEquals(0, BigDecimal("0.75").compareTo(rate.pricePerKm))
    assertEquals(0, BigDecimal("0.20").compareTo(rate.pricePerKg))
    assertEquals(0, BigDecimal("15.00").compareTo(rate.pricePerM3))
    assertEquals(0, BigDecimal("400.00").compareTo(rate.minimumPrice))
    assertEquals(0, BigDecimal("1.25").compareTo(rate.distanceMultiplier))
    assertEquals("USD", rate.currency.trim)

  @Test
  def findByTransportTypeReturnsNoneWhenNotFound(): Unit =
    val result = repository.findByTransportType(TransportType.BOAT)

    assertTrue(result.isEmpty)

  @Test
  def findAllReturnsAllRates(): Unit =
    val result = repository.findAll()

    assertEquals(2, result.size)
    assertTrue(result.exists(_.transportType == TransportType.RAIL))
    assertTrue(result.exists(_.transportType == TransportType.TRUCK))