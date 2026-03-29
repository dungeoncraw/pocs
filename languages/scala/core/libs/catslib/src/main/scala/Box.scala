case class Box[A](value: A)

given cats.Functor[Box] with
  def map[A, B](fa: Box[A])(f: A => B): Box[B] = Box(f(fa.value))