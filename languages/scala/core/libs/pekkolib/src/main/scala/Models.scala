import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.pekko.http.scaladsl.marshalling.Marshaller
import org.apache.pekko.http.scaladsl.model.HttpEntity
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class ItemRequest(name: String)
case class ItemResponse(message: String)

case class User(id: String, name: String)
case class CreateUser(name: String)

case class Order(id: String, item: String, quantity: Int)
case class CreateOrder(item: String, quantity: Int)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemRequestFormat: RootJsonFormat[ItemRequest] = jsonFormat1(ItemRequest.apply)
  implicit val itemResponseFormat: RootJsonFormat[ItemResponse] = jsonFormat1(ItemResponse.apply)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User.apply)
  implicit val createUserFormat: RootJsonFormat[CreateUser] = jsonFormat1(CreateUser.apply)
  implicit val orderFormat: RootJsonFormat[Order] = jsonFormat3(Order.apply)
  implicit val createOrderFormat: RootJsonFormat[CreateOrder] = jsonFormat2(CreateOrder.apply)
}