package study

final case class IO[+A](run: () => A) {
  def map[B](f: A => B): IO[B] =
    IO(() => f(run()))

  def flatMap[B](f: A => IO[B]): IO[B] =
    IO(() => f(run()).run())
}

object IO {
  def delay[A](a: => A): IO[A] =
    IO(() => a)
}
