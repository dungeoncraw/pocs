import zio.*
import zio.stream.*

final case class User(id: Int, name: String)

trait UserRepo:
  def streamUsers: ZStream[Any, Throwable, User]


object UserRepo:
  val live: ZLayer[Any, Nothing, UserRepo] =
    ZLayer.succeed(new UserRepo:
      def streamUsers: ZStream[Any, Throwable, User] =
        ZStream.fromZIO(
          ZIO.foreachPar(1 to 500)(id => ZIO.succeed(User(id, s"user-$id")))
        ).flatMap(ZStream.fromIterable)
    )
