import cats.effect.{IO, IOApp}
import cats.Functor
import cats.syntax.functor.*

def concatHappy[F[_]: Functor](fa: F[String]): F[String] =
  fa.map(_ + ", Happy from learning scala")

object Effects extends IOApp.Simple:
  def run: IO[Unit] =
    for
      _ <- IO.println("Name:")
      name <- IO.readLine
      cleaned = concatHappy(IO.pure(name.trim))
      finalName <- cleaned
      _ <- IO.println(s"Hello, $finalName!")
    yield ()