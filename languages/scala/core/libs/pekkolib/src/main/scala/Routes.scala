import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout

import scala.concurrent.duration.*

class Routes(userActor: ActorRef[UserActor.Command], orderActor: ActorRef[OrderActor.Command])(using system: ActorSystem[_]) extends JsonSupport {

  private implicit val timeout: Timeout = Timeout(5.seconds)

  val route: Route =
    concat(
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello from Pekko!"))
        }
      },
      pathPrefix("users") {
        concat(
          post {
            entity(as[CreateUser]) { req =>
              onSuccess(userActor.ask[User](ref => UserActor.CreateNewUser(req.name, ref))) { user =>
                complete(StatusCodes.Created, user)
              }
            }
          },
          path(Segment) { id =>
            get {
              onSuccess(userActor.ask[Option[User]](ref => UserActor.GetUser(id, ref))) {
                case Some(user) => complete(user)
                case None => complete(StatusCodes.NotFound)
              }
            }
          }
        )
      },
      pathPrefix("orders") {
        concat(
          post {
            entity(as[CreateOrder]) { req =>
              onSuccess(orderActor.ask[Order](ref => OrderActor.CreateNewOrder(req.item, req.quantity, ref))) { order =>
                complete(StatusCodes.Created, order)
              }
            }
          },
          path(Segment) { id =>
            get {
              onSuccess(orderActor.ask[Option[Order]](ref => OrderActor.GetOrder(id, ref))) {
                case Some(order) => complete(order)
                case None => complete(StatusCodes.NotFound)
              }
            }
          }
        )
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