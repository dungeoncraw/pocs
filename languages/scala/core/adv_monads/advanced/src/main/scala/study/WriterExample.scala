package study

object WriterExample:
  private def log(msg: String): Writer[String, Unit] =
    Writer.tell(msg)

  def multiply(x: Int, y: Int): Writer[String, Int] =
    for
      _ <- log(s"Multiplying $x and $y")
      res = x * y
      _ <- log(s"Result is $res")
    yield res

  def run(): Unit =
    val (result, logs) = multiply(3, 4).run
    println(s"Writer Example: Result = $result, Logs = $logs")
