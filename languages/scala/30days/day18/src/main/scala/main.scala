package com.tetokeguii.day18

@main
def advancedLazyExamples(): Unit = {
  println("Infinite Sequences with LazyList (Fibonacci)")

  // The #:: operator (cons) constructs the list lazily.
  lazy val fibs: LazyList[BigInt] =
    BigInt(0) #:: BigInt(1) #:: fibs.zip(fibs.tail).map { case (a, b) => a + b }

  println(s"First 10 Fibonacci: ${fibs.take(10).toList}")

  println(s"Fibonacci (100 to 105): ${fibs.slice(100, 105).toList}")


  println("\n View vs List")

  def heavyComputation(i: Int): String = {
    Thread.sleep(10)
    s"Result-$i"
  }

  val searchSpace = 1 to 500
  val targetId = 5

  println(s"Searching for transformed item 'Result-$targetId'...")

  // .map() is eager. It will run heavyComputation for ALL 500 items
  // BEFORE passing the result to .find().
  print("List (Eager): Processing... ")
  val t0 = System.currentTimeMillis()

  val resList = searchSpace.toList
    .map(heavyComputation)
    .find(_ == s"Result-$targetId")

  val t1 = System.currentTimeMillis()
  println(s"Done in ${t1 - t0} ms")

  // The View creates only a 'recipe'. .map executes nothing immediately.
  // .find() starts pulling elements. .map runs only for item 1, then item 2...
  // Upon reaching 5, .find finds the match and STOPS. The rest (6 to 500) is never processed.
  print("View (Lazy):  Processing... ")
  val t2 = System.currentTimeMillis()

  val resView = searchSpace.view
    .map(heavyComputation)
    .find(_ == s"Result-$targetId")

  val t3 = System.currentTimeMillis()
  println(s"Done in ${t3 - t2} ms")
}