import zio._

trait UserService {
  def getUser(id: Int): UIO[String]
  def searchUsers(term: String): UIO[String]
}

object UserService {
  val live: ULayer[UserService] =
    ZLayer.succeed(new UserService {
      override def getUser(id: Int): UIO[String] =
        ZIO.succeed(s"""{"id": $id, "name": "User$id"}""")

      override def searchUsers(term: String): UIO[String] =
        ZIO.succeed(s"""{"query": "$term", "results": ["Alice", "Bob"]}""")
    })
}