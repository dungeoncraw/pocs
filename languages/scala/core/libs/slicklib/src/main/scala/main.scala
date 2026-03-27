
import slick.jdbc.PostgresProfile.api.*
import scala.concurrent.Await
import scala.concurrent.duration.*
import com.example.slicklib.Tables

val tables = new Tables(slick.jdbc.PostgresProfile)
import tables.*

@main
def main(): Unit = {
  val db = Database.forConfig("postgres")

  try {
    val setupAction = DBIO.seq(
      (users.schema ++ posts.schema).createIfNotExists,

      users += (0, "Alice", "alice@mail.com"),
      users += (0, "Bob", "bob@mail.com"),
      posts ++= Seq(
        (0, "Slick good", 1, 5),
        (0, "PostgreSQL nice", 1, 4),
        (0, "Scala", 2, 3),
        (0, "Future", 2, 5)
      )
    )

    val setupFuture = db.run(setupAction)
    Await.result(setupFuture, 10.seconds)

    val allUsersFuture = db.run(users.result)
    val allUsers = Await.result(allUsersFuture, 2.seconds)
    allUsers.foreach(u => println(s"User: ${u._2} (${u._3})"))

    val highRatedQuery = posts.filter(_.rating >= 5)
    val highRatedPosts = Await.result(db.run(highRatedQuery.result), 2.seconds)
    highRatedPosts.foreach(p => println(s"Post: ${p._2} (Rating: ${p._4})"))

    val complexResult = Await.result(db.run(complexQuery.result), 2.seconds)
    complexResult.foreach { case (name, avgRating, postCount) =>
      println(s"Author: $name, Avg Rating: ${avgRating.getOrElse(0.0)}, Total Posts: $postCount")
    }

  } finally {
    db.close()
  }
}

