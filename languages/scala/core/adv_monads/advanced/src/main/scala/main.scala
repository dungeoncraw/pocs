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

object SimpleIOExample {

  private def putStrLn(s: String): IO[Unit] =
    IO.delay(println(s))

  private val readLine: IO[String] =
    IO.delay(scala.io.StdIn.readLine())

  private val program: IO[Unit] =
    for {
      _    <- putStrLn("What is your name?")
      name <- readLine
      _    <- putStrLn(s"Hello, $name!")
    } yield ()

  def main(args: Array[String]): Unit =
    program.run()
}