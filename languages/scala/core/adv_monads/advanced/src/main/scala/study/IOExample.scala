package study

object IOExample {

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

  def run(): Unit =
    program.run()
}
