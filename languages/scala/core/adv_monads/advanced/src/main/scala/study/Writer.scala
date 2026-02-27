package study

final case class Writer[W, A](run: (A, List[W])):
  def map[B](f: A => B): Writer[W, B] =
    val (a, logs) = run
    Writer((f(a), logs))

  def flatMap[B](f: A => Writer[W, B]): Writer[W, B] =
    val (a, logs1) = run
    val (b, logs2) = f(a).run
    Writer((b, logs1 ++ logs2))

object Writer:
  def pure[W, A](a: A): Writer[W, A] =
    Writer((a, Nil))

  def tell[W](w: W): Writer[W, Unit] =
    Writer(((), List(w)))
