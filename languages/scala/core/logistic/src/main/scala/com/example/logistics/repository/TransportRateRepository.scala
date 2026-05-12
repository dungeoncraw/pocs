package com.example.logistics.repository

import com.example.logistics.domain.{TransportRate, TransportType}
import org.springframework.jdbc.core.{JdbcTemplate, RowMapper}
import org.springframework.stereotype.Repository

import java.sql.ResultSet

@Repository
class TransportRateRepository(jdbcTemplate: JdbcTemplate):

  private val transportRateRowMapper: RowMapper[TransportRate] =
    (rs: ResultSet, _: Int) =>
      TransportRate(
        transportType = TransportType.valueOf(rs.getString("transport_type")),
        basePrice = rs.getBigDecimal("base_price"),
        pricePerKm = rs.getBigDecimal("price_per_km"),
        pricePerKg = rs.getBigDecimal("price_per_kg"),
        pricePerM3 = rs.getBigDecimal("price_per_m3"),
        minimumPrice = rs.getBigDecimal("minimum_price"),
        distanceMultiplier = rs.getBigDecimal("distance_multiplier"),
        currency = rs.getString("currency")
      )

  def findByTransportType(transportType: TransportType): Option[TransportRate] =
    val rates =
      jdbcTemplate.query(
        """
          SELECT
            transport_type,
            base_price,
            price_per_km,
            price_per_kg,
            price_per_m3,
            minimum_price,
            distance_multiplier,
            currency
          FROM transport_rates
          WHERE transport_type = ?
        """,
        transportRateRowMapper,
        transportType.toString
      )

    if rates.isEmpty then None else Some(rates.get(0))

  def findAll(): List[TransportRate] =
    val rates =
      jdbcTemplate.query(
        """
            SELECT
              transport_type,
              base_price,
              price_per_km,
              price_per_kg,
              price_per_m3,
              minimum_price,
              distance_multiplier,
              currency
            FROM transport_rates
            ORDER BY transport_type
          """,
        transportRateRowMapper
      )

    rates.toArray.toList.map(_.asInstanceOf[TransportRate])