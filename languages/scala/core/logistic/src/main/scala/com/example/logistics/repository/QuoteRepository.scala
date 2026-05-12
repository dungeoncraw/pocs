package com.example.logistics.repository

import com.example.logistics.domain.{Quote, TransportType}
import org.springframework.jdbc.core.{JdbcTemplate, RowMapper}
import org.springframework.stereotype.Repository

import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID


@Repository
class QuoteRepository(jdbcTemplate: JdbcTemplate):
  private val quoteRowMapper: RowMapper[Quote] =
    (rs: ResultSet, _: Int) =>
      Quote(
        id = rs.getObject("id", classOf[UUID]),
        customerId = rs.getString("customer_id"),
        transportType = TransportType.valueOf(rs.getString("transport_type")),

        originLatitude = rs.getBigDecimal("origin_latitude"),
        originLongitude = rs.getBigDecimal("origin_longitude"),
        destinationLatitude = rs.getBigDecimal("destination_latitude"),
        destinationLongitude = rs.getBigDecimal("destination_longitude"),

        weightKg = rs.getBigDecimal("weight_kg"),
        lengthCm = rs.getBigDecimal("length_cm"),
        widthCm = rs.getBigDecimal("width_cm"),
        heightCm = rs.getBigDecimal("height_cm"),

        straightDistanceKm = rs.getBigDecimal("straight_distance_km"),
        distanceKm = rs.getBigDecimal("distance_km"),
        volumeM3 = rs.getBigDecimal("volume_m3"),

        basePrice = rs.getBigDecimal("base_price"),
        distanceCost = rs.getBigDecimal("distance_cost"),
        weightCost = rs.getBigDecimal("weight_cost"),
        volumeCost = rs.getBigDecimal("volume_cost"),
        minimumPrice = rs.getBigDecimal("minimum_price"),
        finalPrice = rs.getBigDecimal("final_price"),

        currency = rs.getString("currency"),

        createdAt = rs.getObject("created_at", classOf[OffsetDateTime]),
        expiresAt = rs.getObject("expires_at", classOf[OffsetDateTime])
      )

  def save(quote: Quote): Quote =
    jdbcTemplate.update(
      """
        INSERT INTO quotes (
          id,
          customer_id,
          transport_type,
          origin_latitude,
          origin_longitude,
          destination_latitude,
          destination_longitude,
          weight_kg,
          length_cm,
          width_cm,
          height_cm,
          straight_distance_km,
          distance_km,
          volume_m3,
          base_price,
          distance_cost,
          weight_cost,
          volume_cost,
          minimum_price,
          final_price,
          currency,
          created_at,
          expires_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      """,
      quote.id,
      quote.customerId,
      quote.transportType.toString,
      quote.originLatitude,
      quote.originLongitude,
      quote.destinationLatitude,
      quote.destinationLongitude,
      quote.weightKg,
      quote.lengthCm,
      quote.widthCm,
      quote.heightCm,
      quote.straightDistanceKm,
      quote.distanceKm,
      quote.volumeM3,
      quote.basePrice,
      quote.distanceCost,
      quote.weightCost,
      quote.volumeCost,
      quote.minimumPrice,
      quote.finalPrice,
      quote.currency,
      quote.createdAt,
      quote.expiresAt
    )

    quote

  def findById(id: UUID): Option[Quote] =
    val quotes =
      jdbcTemplate.query(
        """
          SELECT
            id,
            customer_id,
            transport_type,
            origin_latitude,
            origin_longitude,
            destination_latitude,
            destination_longitude,
            weight_kg,
            length_cm,
            width_cm,
            height_cm,
            straight_distance_km,
            distance_km,
            volume_m3,
            base_price,
            distance_cost,
            weight_cost,
            volume_cost,
            minimum_price,
            final_price,
            currency,
            created_at,
            expires_at
          FROM quotes
          WHERE id = ?
        """,
        quoteRowMapper,
        id
      )

    if quotes.isEmpty then None else Some(quotes.get(0))