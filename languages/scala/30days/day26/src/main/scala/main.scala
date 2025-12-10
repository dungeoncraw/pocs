import scala.io.Source
import scala.util.{Try, Using}

@main
def main(): Unit = {
  val ten1 = Try(10 /0)
  println(s"ten1 Error = $ten1")
  val ten2 = Try(10 / 2)
  println(s"ten2 Success = $ten2")
  val ten3 = Try(10 / 2).toEither
  println(s"ten3 Right(success) = $ten3")

  def loadFile(path: String): Either[String, String] = {
    val resource = this.getClass.getClassLoader.getResource(path)
    if (resource == null) return Left(s"File not found: $path")
    Using(Source.fromURL(resource))(_.mkString).toEither
      .left.map(_.getMessage)
  }

  loadFile("sample.txt") match {
    case Right(content) => println(s"content = $content")
    case Left(error) => println(s"error = $error")
  }

  loadFile("unknown-file.txt") match {
    case Right(content) => println(s"content = $content")
    case Left(error) => println(s"error = $error")
  }
}