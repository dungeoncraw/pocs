package study

object ReaderExample:
  case class Config(user: String, port: Int)

  private def getUser: Reader[Config, String] =
    Reader.ask[Config].map(_.user)

  private def getPort: Reader[Config, Int] =
    Reader.ask[Config].map(_.port)

  def program: Reader[Config, String] =
    for
      user <- getUser
      port <- getPort
    yield s"Connecting as $user to port $port"

  def run(): Unit =
    val config = Config("admin", 8080)
    println(s"Reader Example: ${program.run(config)}")
