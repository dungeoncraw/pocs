package com.tetokeguii.day14

enum MyList[+A]:
  case Cons(head: A, tail: MyList[A])
  case Nil

final case class Box[A](value: A)
final case class Pair[A, B](first: A, second: B)

// match type for pattern matching
type ElemOf[X] = X match
  case String          => Char
  case List[t]         => t
  case MyList[t]       => t
  case Box[t]          => t
  case Pair[a, b]      => a
  case _               => X

def firstOf[X](x: X): ElemOf[X] = x match
  case s: String =>
    s.head.asInstanceOf[ElemOf[X]]

  case list: List[?] =>
    list.head.asInstanceOf[ElemOf[X]]

  case myList: MyList[?] =>
    myList match
      case MyList.Cons(h, _) => h.asInstanceOf[ElemOf[X]]
      case MyList.Nil        => throw Exception("empty MyList")

  case box: Box[?] =>
    box.value.asInstanceOf[ElemOf[X]]

  case pair: Pair[?, ?] =>
    pair.first.asInstanceOf[ElemOf[X]]

  case other =>
    other.asInstanceOf[ElemOf[X]]

@main def demo(): Unit =
  val s: String        = "Scala"
  val l: List[Int]     = List(10, 20, 30)
  val ml: MyList[Int]  = MyList.Cons(1, MyList.Cons(2, MyList.Nil))
  val b: Box[Double]   = Box(3.14)
  val p: Pair[String, Int] = Pair("hello", 42)
  val n: Int           = 99

  val c: Char    = firstOf(s)
  val i: Int     = firstOf(l)
  val mi: Int    = firstOf(ml)
  val d: Double  = firstOf(b)
  val str: String= firstOf(p)
  val same: Int  = firstOf(n)

  println(c)
  println(i)
  println(mi)
  println(d)
  println(str)
  println(same)