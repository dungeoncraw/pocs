import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object Routes {

  val route: Route =
    concat(
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello from Pekko!"))
        }
      },
      path("items") {
        post {
          entity(as[String]) { body =>
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Created item with body: $body"))
          }
        }
      },
      path("items" / Segment) { id =>
        delete {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Deleted item with id: $id"))
        }
      }
    )
}