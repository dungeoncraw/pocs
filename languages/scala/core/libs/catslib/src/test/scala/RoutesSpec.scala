import cats.effect.IO
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import munit.CatsEffectSuite
import io.circe.generic.auto.*

class RoutesSpec extends CatsEffectSuite {

  test("POST /monoid with valid JSON should return 200 OK") {
    val body = """{"count": 10, "sum": 20.0}"""
    val request = Request[IO](Method.POST, uri"/monoid").withEntity(body)(using EntityEncoder.stringEncoder)
    
    Routes.allRoutes.orNotFound.run(request).flatMap { response =>
      assertEquals(response.status, Status.Ok)
      response.as[Metrics].map { metrics =>
        assertEquals(metrics, Metrics(11, 30.0)) // (10, 20) + (1, 10)
      }
    }
  }

  test("POST /monoid with malformed JSON (single quotes) should return 400 Bad Request") {
    val body = "{'count': 10, 'sum': 20}"
    val request = Request[IO](Method.POST, uri"/monoid").withEntity(body)(using EntityEncoder.stringEncoder)
    
    Routes.allRoutes.orNotFound.run(request).attempt.map {
      case Left(error: MessageFailure) => assertEquals(error.toHttpResponse(HttpVersion.`HTTP/1.1`).status, Status.BadRequest)
      case Right(response) => assertEquals(response.status, Status.BadRequest)
      case Left(error) => fail(s"Expected a MessageFailure but got ${error.getClass.getName}: ${error.getMessage}")
    }
  }
}
