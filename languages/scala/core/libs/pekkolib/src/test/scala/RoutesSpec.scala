import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class RoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "Routes" should {

    "return hello message on GET /hello" in {
      Get("/hello") ~> Routes.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Hello from Pekko!"
      }
    }

    "create an item on POST /items" in {
      Post("/items", "my new item") ~> Routes.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Created item with body: my new item"
      }
    }

    "delete an item on DELETE /items/{id}" in {
      Delete("/items/123") ~> Routes.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Deleted item with id: 123"
      }
    }
  }
}