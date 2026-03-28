import cats.*
import cats.syntax.eq.*
import cats.syntax.show.*


case class User(name: String, age: Int)

given Eq[User] = Eq.instance { (x, y) =>
  x.name === y.name && x.age === y.age
}

given Show[User] with
  def show(user: User): String = s"Name of user =${user.name}, age=${user.age}"