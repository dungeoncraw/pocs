import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.http.scaladsl.server.Route

class RoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  val testKit = ActorTestKit()
  val userActor: ActorRef[UserActor.Command] = testKit.spawn(UserActor())
  val orderActor: ActorRef[OrderActor.Command] = testKit.spawn(OrderActor())
  val routes: Route = new Routes(userActor, orderActor)(using testKit.system).route

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "Routes" should {

    "return hello message on GET /hello" in {
      Get("/hello") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Hello from Pekko!"
      }
    }

    "create and get a user" in {
      val userRequest = CreateUser("John Doe")
      Post("/users", userRequest) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        val user = responseAs[User]
        user.name shouldBe "John Doe"

        Get(s"/users/${user.id}") ~> routes ~> check {
          status shouldBe StatusCodes.OK
          responseAs[User] shouldBe user
        }
      }
    }

    "create and get an order" in {
      val orderRequest = CreateOrder("Laptop", 1)
      Post("/orders", orderRequest) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        val order = responseAs[Order]
        order.item shouldBe "Laptop"
        order.quantity shouldBe 1

        Get(s"/orders/${order.id}") ~> routes ~> check {
          status shouldBe StatusCodes.OK
          responseAs[Order] shouldBe order
        }
      }
    }

    "return 404 for non-existent user" in {
      Get("/users/unknown") ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "create an item on POST /items" in {
      Post("/items", "my new item") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Created item with body: my new item"
      }
    }

    "delete an item on DELETE /items/{id}" in {
      Delete("/items/123") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Deleted item with id: 123"
      }
    }
  }
}