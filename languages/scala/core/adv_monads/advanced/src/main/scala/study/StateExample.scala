package study

object StateExample:

  private val nextInt: State[Int, Int] =
    State(n => (n + 1, n))

  private val nextInSequence: State[Int, (Int, Int, Int)] =
    for
      a <- nextInt
      b <- nextInt
      c <- nextInt
    yield (a, b, c)

  /**
   * SAME AS
   * val program =
   *   nextInt.flatMap { a =>
   *     nextInt.flatMap { b =>
   *       nextInt.map { c =>
   *          (a, b, c)
   *       }
   *     }
   *   }
   */
  private val peekThenNext: State[Int, (Int, Int)] =
    for
      before <- State.get[Int]  // current counter
      n      <- nextInt         // increments
    yield (before, n)

  private val jumpByTen: State[Int, (Int, Int)] =
    for
      a <- nextInt // set to 11
      _ <- State.modify[Int](_ + 10) // jump by 10, so become 21
      b <- nextInt // set to 21
    yield (a, b)

  def run(): Unit =
    val (finalState, result) = nextInSequence.run(0)
    val (before, n) = peekThenNext.run(10)
    val (before2, n2) = jumpByTen.run(10)
    println(s"StateExample result: $result, final state: $finalState")
    println(s"peekThenNext result: ($before, $n)")
    println(s"jumpByTen result: ($before2, $n2)")