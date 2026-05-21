import com.example.logistics.domain.*
import com.example.logistics.service.QuoteCalculator

@main def runLogistics(): Unit =
  val request = QuoteRequest(
    origin = Location(lat = -23.5505, lon = -46.6333), // São Paulo
    destination = Location(lat = -22.9068, lon = -43.1729), // Rio de Janeiro
    weightKg = 100.0,
    dimensions = Dimensions(lengthCm = 50, widthCm = 50, heightCm = 50),
    transportType = TransportType.Truck
  )

  val heavyRequest = request.copy(weightKg = 30000.0)
  val tallRequest = request.copy(dimensions = Dimensions(50, 50, 500))
  val bigVolumeRequest = request.copy(dimensions = Dimensions(600, 600, 300), transportType = TransportType.Truck)

  println("-" * 30)
  println("Logistics Quote Results")
  println("-" * 30)
  
  processQuote(QuoteCalculator.calculate(request))
  processQuote(QuoteCalculator.calculate(heavyRequest))
  processQuote(QuoteCalculator.calculate(tallRequest))
  processQuote(QuoteCalculator.calculate(bigVolumeRequest))

def processQuote(result: Either[String, Quote]): Unit =
  result match
    case Left(error) =>
      println(s"ERROR: $error")
      println("-" * 30)
    case Right(quote) =>
      println(s"ID: ${quote.id}")
      println(s"Transport: ${quote.request.transportType}")
      println(s"Distance: ${"%.2f".format(quote.distanceKm)} km")
      println(s"Weight: ${quote.request.weightKg} kg")
      println(s"Volume: ${"%.4f".format(quote.request.dimensions.volumeM3)} m3")
      println("-" * 30)
      println(s"TOTAL PRICE: ${"%.2f".format(quote.totalPrice)}")
      println("-" * 30)
