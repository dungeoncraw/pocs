import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import java.util.UUID

object UserActor {
  sealed trait Command
  case class CreateNewUser(name: String, replyTo: ActorRef[User]) extends Command
  case class GetUser(id: String, replyTo: ActorRef[Option[User]]) extends Command

  def apply(): Behavior[Command] = registry(Map.empty)

  private def registry(users: Map[String, User]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateNewUser(name, replyTo) =>
        val id = UUID.randomUUID().toString
        val newUser = User(id, name)
        replyTo ! newUser
        registry(users + (id -> newUser))
      case GetUser(id, replyTo) =>
        replyTo ! users.get(id)
        Behaviors.same
    }
}

object OrderActor {
  sealed trait Command
  case class CreateNewOrder(item: String, quantity: Int, replyTo: ActorRef[Order]) extends Command
  case class GetOrder(id: String, replyTo: ActorRef[Option[Order]]) extends Command

  def apply(): Behavior[Command] = registry(Map.empty)

  private def registry(orders: Map[String, Order]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateNewOrder(item, quantity, replyTo) =>
        val id = UUID.randomUUID().toString
        val newOrder = Order(id, item, quantity)
        replyTo ! newOrder
        registry(orders + (id -> newOrder))
      case GetOrder(id, replyTo) =>
        replyTo ! orders.get(id)
        Behaviors.same
    }
}
