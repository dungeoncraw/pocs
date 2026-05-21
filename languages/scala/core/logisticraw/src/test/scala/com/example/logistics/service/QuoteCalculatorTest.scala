package com.example.logistics.service

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.logistics.domain.*
import com.example.logistics.provider.TransportRateProvider

class QuoteCalculatorTest extends AnyFunSuite with Matchers {

  val origin = Location(-23.5505, -46.6333) // São Paulo
  val destination = Location(-22.9068, -43.1729) // Rio de Janeiro

  test("Truck: should calculate quote successfully for valid request") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 1000,
      dimensions = Dimensions(100, 100, 100),
      transportType = TransportType.Truck
    )
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice should be > 0.0
      quote.request.transportType shouldBe TransportType.Truck
    }
  }

  test("Truck: should fail if weight exceeds maximum capacity") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 30000,
      dimensions = Dimensions(100, 100, 100),
      transportType = TransportType.Truck
    )
    val result = QuoteCalculator.calculate(request)
    result.isLeft shouldBe true
    result.left.getOrElse("") should include("Weight 30000.0kg exceeds maximum capacity")
  }

  test("Truck: should fail if height exceeds maximum capacity") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 1000,
      dimensions = Dimensions(100, 100, 500),
      transportType = TransportType.Truck
    )
    val result = QuoteCalculator.calculate(request)
    result.isLeft shouldBe true
    result.left.getOrElse("") should include("Height 500.0cm exceeds maximum capacity")
  }

  test("Rail: should calculate quote successfully for valid request") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 50000,
      dimensions = Dimensions(200, 200, 400),
      transportType = TransportType.Rail
    )
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice should be > 0.0
      quote.request.transportType shouldBe TransportType.Rail
    }
  }

  test("Boat: should calculate quote successfully for valid request") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 500000,
      dimensions = Dimensions(500, 500, 800),
      transportType = TransportType.Boat
    )
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice should be > 0.0
      quote.request.transportType shouldBe TransportType.Boat
    }
  }

  test("Motorcycle: should calculate quote successfully for valid request") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 50,
      dimensions = Dimensions(50, 30, 40),
      transportType = TransportType.Motorcycle
    )
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice should be > 0.0
      quote.request.transportType shouldBe TransportType.Motorcycle
    }
  }

  test("Plane: should calculate quote successfully for valid request") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 10000,
      dimensions = Dimensions(300, 300, 300),
      transportType = TransportType.Plane
    )
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice should be > 0.0
      quote.request.transportType shouldBe TransportType.Plane
    }
  }

  test("Motorcycle: should fail if volume exceeds maximum capacity") {
    val request = QuoteRequest(
      origin = origin,
      destination = destination,
      weightKg = 10,
      dimensions = Dimensions(100, 100, 100),
      transportType = TransportType.Motorcycle
    )
    val result = QuoteCalculator.calculate(request)
    result.isLeft shouldBe true
    result.left.getOrElse("") should include("Volume 1.0m3 exceeds maximum capacity of 0.5m3")
  }

  test("Minimum price should be applied") {
    val nearOrigin = Location(0, 0)
    val nearDestination = Location(0, 0.01)
    val request = QuoteRequest(
      origin = nearOrigin,
      destination = nearDestination,
      weightKg = 1,
      dimensions = Dimensions(1, 1, 1),
      transportType = TransportType.Rail
    )
    val rate = TransportRateProvider.getRate(TransportType.Rail)
    val result = QuoteCalculator.calculate(request)
    result.isRight shouldBe true
    result.foreach { quote =>
      quote.totalPrice shouldBe rate.minimumPrice
    }
  }
}
