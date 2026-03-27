import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api.*
import scala.concurrent.Await
import scala.concurrent.duration.*
import com.example.slicklib.Tables

class SlickSpec extends AnyFlatSpec with Matchers {
  val tables = new Tables(slick.jdbc.H2Profile)
  import tables.*

  val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

  "Slick tables" should "be able to insert and query data" in {
    val setup = DBIO.seq(
      (users.schema ++ posts.schema).create,
      users += (1, "Alice", "alice@test.com"),
      posts += (1, "Test Post", 1, 5)
    )

    Await.result(db.run(setup), 5.seconds)

    val result = Await.result(db.run(users.result), 5.seconds)
    result should have size 1
    result.head._2 shouldBe "Alice"
  }

  it should "execute complex query correctly" in {
    val data = DBIO.seq(
      users += (2, "Bob", "bob@test.com"),
      posts ++= Seq(
        (2, "Post 1", 2, 4),
        (3, "Post 2", 2, 4)
      )
    )

    Await.result(db.run(data), 5.seconds)

    val result = Await.result(db.run(complexQuery.result), 5.seconds)
    
    // Alice has one post with rating 5 (Avg 5) -> should be in
    // Bob has two posts with rating 4 (Avg 4) -> should be in
    result should have size 2
    
    val aliceResult = result.find(_._1 == "Alice").get
    aliceResult._2 shouldBe Some(5)
    aliceResult._3 shouldBe 1

    val bobResult = result.find(_._1 == "Bob").get
    bobResult._2 shouldBe Some(4)
    bobResult._3 shouldBe 2
  }
}
