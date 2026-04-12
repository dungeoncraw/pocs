import zio._
import zio.http._

object HttpServer extends ZIOAppDefault {

  private val helloRoute: Route[Any, Response] =
    Method.GET / "hello" -> handler {
      Response.text("Hello from ZIO HTTP!")
    }

  private val userRoute: Route[UserService, Response] =
    Method.GET / "users" / int("id") -> handler { (id: Int, req: Request) =>
      ZIO.serviceWithZIO[UserService](_.getUser(id)).map(Response.text)
    }

  private val searchRoute: Route[UserService, Response] =
    Method.GET / "search" -> handler { (req: Request) =>
      val term =
        req.queryParam("term").getOrElse("unknown")

      ZIO.serviceWithZIO[UserService](_.searchUsers(term)).map(Response.text)
    }

  private val app: Routes[UserService, Response] =
    Routes(helloRoute, userRoute, searchRoute)

  override def run: ZIO[Any, Throwable, Unit] =
    Server
      .serve(app)
      .provide(
        Server.default,
        UserService.live
      )
}