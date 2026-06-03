final case class UserId(value: String)

// by default, case class is immutable, so we can't change the value of the fields
final case class User(
                       id: UserId,
                       name: String,
                       email: String
                     ):
  // case class can have methods
  def changeEmail(newEmail: String): User =
    copy(email = newEmail)
// case class can have companion objects to validate input data
object User:
  def from(name: String, email: String): Either[String, User] =
    if (email.contains("@"))
      Right(User(UserId(java.util.UUID.randomUUID().toString), name, email))
    else
      Left("Invalid email format")

// no data is passed to the constructor, so we can't create a new instance of User
case object UserRepository:
  def findById(id: UserId): Option[User] =
    // implementation to find user by id
    None

@main
def main(): Unit =
  val user1 = User(
    id = UserId("u-1"),
    name = "Thiago",
    email = "thiago@example.com"
  )

  val user2 = user1.copy(name = "Thiago Jr.")
  // user2.name is not accessible
  println(user1.name)

  println(user1 == user2)

  println(user1)

  val updatedUser = user1.changeEmail("new-email@example.com")

  println(updatedUser)

  user1 match
    case User(UserId(id), name, email) =>
      println(s"User id: $id, name: $name, email: $email")

  println(UserRepository.findById(user1.id))

  val user3 = User.from("John", "john")
  println(user3)