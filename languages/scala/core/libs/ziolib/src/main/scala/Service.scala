import zio.*

trait Service:
  def extractMiddleLetter(value: String): UIO[Char]

object Service:
  val live: ZLayer[Any, Nothing, Service] =
    ZLayer.succeed(new Service:
      def extractMiddleLetter(value: String): UIO[Char] =
        ZIO.succeed(value.charAt(value.length / 2))
    )