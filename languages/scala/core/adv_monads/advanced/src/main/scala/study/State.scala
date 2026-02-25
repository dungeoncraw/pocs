package study

final case class State[S, A](run: S => (S, A)):
  def map[B](f: A => B): State[S, B] =
    State(s =>
      val (s2, a) = run(s)
      (s2, f(a))
    )

  def flatMap[B](f: A => State[S, B]): State[S, B] =
    State(s =>
      val (s2, a) = run(s)
      f(a).run(s2)
    )

object State:
  def pure[S, A](a: A): State[S, A] =
    State(s => (s, a))

  def get[S]: State[S, S] =
    State(s => (s, s))

  def modify[S](f: S => S): State[S, Unit] =
    State(s => (f(s), ()))
