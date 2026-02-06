final case class User(name: String, age: Int)

def parseInt(s: String): Either[String, Int] =
  // to intOption don't throw error, return None if not an int
  // toRight creates Left(msg) if None
  s.toIntOption.toRight(s"Not an int: '$s'")

def nonBlank(label: String)(s: String): Either[String, String] =
  val t = s.trim
  if t.nonEmpty then Right(t)
  else Left(s"$label must be non-empty")

def nonNegativeAge(n: Int): Either[String, Int] =
  if n >= 0 then Right(n)
  else Left(s"Age must be >= 0, got $n")

// fail-fast -> stops at the first Left, no error accumulation.
def makeUser(nameRaw: String, ageRaw: String): Either[String, User] =
  for
    name <- nonBlank("name")(nameRaw)
    age  <- parseInt(ageRaw)
    ok   <- nonNegativeAge(age)
  yield User(name, ok)

def demoFailFast(): Unit =
  // invalid input: name empties, age not int
  // but as fail fast, get only the name empty error
  val input = ("   ", "nope")
  val result = makeUser(input._1, input._2)
  println(s"makeUser(${input._1}, ${input._2}) = $result")

def parseIntUnsafe(s: String): Either[String, Int] =
  Right(s.toInt) // throws NumberFormatException for non-ints

def demoExceptionsEither(): Unit =
  try
    val r = parseIntUnsafe("nope")
    println(s"parseIntUnsafe(nope) => $r (never reach this line)")
  catch
    case e: NumberFormatException =>
      println(s"Crashed with exception: ${e.getClass.getSimpleName}: ${e.getMessage}\n")

@main
def main(): Unit = {
  println("=== Either monad: examples ===\n")
  demoFailFast()
  demoExceptionsEither()
  println("Done.")
}

