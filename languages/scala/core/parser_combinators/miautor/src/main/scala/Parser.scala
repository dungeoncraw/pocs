package parser

import scala.util.matching.Regex

enum ParseResult[+A]:
  case Success(value: A, nextInput: String)
  case Failure(message: String, input: String)

case class Parser[+A](run: String => ParseResult[A]):
  def map[B](f: A => B): Parser[B] =
    Parser { input =>
      run(input) match
        case ParseResult.Success(value, next) => ParseResult.Success(f(value), next)
        case ParseResult.Failure(msg, input) => ParseResult.Failure(msg, input)
    }

  def flatMap[B](f: A => Parser[B]): Parser[B] =
    Parser { input =>
      run(input) match
        case ParseResult.Success(value, next) => f(value).run(next)
        case ParseResult.Failure(msg, input)  => ParseResult.Failure(msg, input)
    }

  def |[B >: A](other: => Parser[B]): Parser[B] =
    Parser { input =>
      run(input) match
        case ParseResult.Failure(_, _) => other.run(input)
        case success                   => success
    }

  def ~[B](other: => Parser[B]): Parser[(A, B)] =
    for
      a <- this
      b <- other
    yield (a, b)

  def ~>[B](other: => Parser[B]): Parser[B] =
    for
      _ <- this
      b <- other
    yield b

  def <~[B](other: => Parser[B]): Parser[A] =
    for
      a <- this
      _ <- other
    yield a

  def many: Parser[List[A]] =
    Parser { input =>
      def loop(currentInput: String, acc: List[A]): ParseResult[List[A]] =
        run(currentInput) match
          case ParseResult.Success(value, next) => 
            if (next == currentInput) ParseResult.Success(acc.reverse, next) // Prevent infinite loop
            else loop(next, value :: acc)
          case ParseResult.Failure(_, _) => ParseResult.Success(acc.reverse, currentInput)
      loop(input, Nil)
    }

  def many1: Parser[List[A]] =
    for
      first <- this
      rest <- many
    yield first :: rest

  def opt: Parser[Option[A]] =
    this.map(Some(_)) | Parser.pure(None)

object Parser:
  def pure[A](value: A): Parser[A] =
    Parser(input => ParseResult.Success(value, input))

  def fail(msg: String): Parser[Nothing] =
    Parser(input => ParseResult.Failure(msg, input))

  def char(expected: Char): Parser[Char] =
    Parser { input =>
      if input.nonEmpty && input.head == expected then
        ParseResult.Success(expected, input.tail)
      else
        ParseResult.Failure(s"Expected '$expected' but found '${if input.isEmpty then "EOF" else input.head}'", input)
    }

  def string(expected: String): Parser[String] =
    Parser { input =>
      if input.startsWith(expected) then
        ParseResult.Success(expected, input.substring(expected.length))
      else
        ParseResult.Failure(s"Expected '$expected' but found '${input.take(expected.length)}'", input)
    }

  def regex(r: Regex): Parser[String] =
    Parser { input =>
      r.findPrefixOf(input) match
        case Some(matched) => ParseResult.Success(matched, input.substring(matched.length))
        case None          => ParseResult.Failure(s"Regex $r did not match", input)
    }

  def whitespace: Parser[String] = regex("""\s*""".r)

  def sepBy[A, B](p: Parser[A], sep: Parser[B]): Parser[List[A]] =
    (p ~ (sep ~> p).many).map((first, rest) => first :: rest) | pure(Nil)

  def recursive[A](p: => Parser[A]): Parser[A] =
    lazy val parser = p
    Parser(input => parser.run(input))
