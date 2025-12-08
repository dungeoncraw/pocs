
// A function that takes a polymorphic function
def runOnBothTypes(f: [A] => A => A): (Int, String) = {
  val intResult = f[Int](42)
  val stringResult = f[String]("hello")
  (intResult, stringResult)
}

@main
def main(): Unit = {
  val identity: [A] => A => A = [A] => (x: A) => x
  val double: [A] => A => A = [A] => (x: A) => x match {
    case i: Int => (i * 2).asInstanceOf[A]
    case s: String => (s + s).asInstanceOf[A]
    case _ => x
  }
  println(runOnBothTypes(identity))
  println(runOnBothTypes(double))
}