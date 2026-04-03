import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import cats.syntax.all.*
import cats.Monoid

object Routes {

  private def helloRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" =>
      Ok("Hello from http4s")
  }

  private def loginRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "login" =>
      for {
        user <- req.as[User]
        resp <- Ok(s"Welcome ${user.name}!")
      } yield resp
  }

  private def functorRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "functor" / name =>
      val box = Box(name)
      val mappedBox = box.map(_.toUpperCase)
      Ok(s"Original: ${box.value}, Mapped: ${mappedBox.value}")
  }

  private def monoidRoute(using Monoid[Metrics]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "monoid" =>
        for {
          metrics <- req.as[Metrics]
          combined = metrics |+| Metrics(1, 10.0)
          resp <- Ok(combined)
        } yield resp
    }

  def allRoutes: HttpRoutes[IO] =
    helloRoute <+> loginRoute <+> functorRoute <+> monoidRoute
}