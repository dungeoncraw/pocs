
import parser.*

@main
def main(): Unit = {
  val space: Parser[String] = Parser.whitespace
  val number: Parser[Int] = (space ~> Parser.regex("[0-9]+".r) <~ space).map(_.toInt)

  def char(c: Char): Parser[Char] = space ~> Parser.char(c) <~ space

  def expression: Parser[Int] = Parser.recursive {
    (term ~ ((char('+') | char('-')) ~ term).many).map { (first, rest) =>
      rest.foldLeft(first) {
        case (acc, ('+', next)) => acc + next
        case (acc, ('-', next)) => acc - next
        case (acc, _)           => acc
      }
    }
  }

  def term: Parser[Int] = Parser.recursive {
    (factor ~ ((char('*') | char('/')) ~ factor).many).map { (first, rest) =>
      rest.foldLeft(first) {
        case (acc, ('*', next)) => acc * next
        case (acc, ('/', next)) => acc / next
        case (acc, _)           => acc
      }
    }
  }

  def factor: Parser[Int] = 
    number | (char('(') ~> expression <~ char(')'))

  val parser = expression

  val inputs = List(
    "1 + 2 * 3",
    "(1 + 2) * 3",
    "10 - 2 - 3",
    "100 / 2 / 2",
    " 42 "
  )

  inputs.foreach { input =>
    parser.run(input) match
      case ParseResult.Success(result, "") => println(s"'$input' = $result")
      case ParseResult.Success(result, rem) => println(s"'$input' = $result (remaining: '$rem')")
      case ParseResult.Failure(msg, _) => println(s"'$input' failed: $msg")
  }
}

