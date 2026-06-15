package socialmedia.service

import socialmedia.domain.*

final case class StateResult[S, E, A](run: S => Either[E, (S, A)]) {
  def map[B](f: A => B): StateResult[S, E, B] =
    StateResult { s =>
      run(s).map { case (nextS, a) => (nextS, f(a)) }
    }

  def flatMap[B](f: A => StateResult[S, E, B]): StateResult[S, E, B] =
    StateResult { s =>
      run(s).flatMap { case (nextS, a) => f(a).run(nextS) }
    }
}

object StateResult {
  def pure[S, E, A](a: A): StateResult[S, E, A] =
    StateResult(s => Right((s, a)))

  def error[S, E, A](e: E): StateResult[S, E, A] =
    StateResult(_ => Left(e))

  def inspect[S, E, A](f: S => A): StateResult[S, E, A] =
    StateResult(s => Right((s, f(s))))

  def modify[S, E](f: S => S): StateResult[S, E, Unit] =
    StateResult(s => Right((f(s), ())))

  def get[S, E]: StateResult[S, E, S] =
    StateResult(s => Right((s, s)))
}

type AppStateResult[A] = StateResult[AppState, AppError, A]
