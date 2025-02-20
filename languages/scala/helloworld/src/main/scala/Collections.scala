import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@main
def collections(): Unit = {
  val l = List(1, 2, 3, 2, 3)
  val l2 = List(1, true, "some string")
  println(l)
  println(l2)

  // empty lists can be Nil
  val n = List()
  val n2 = Nil

  println(n)
  println(n2)

  val typed = List[Int](1, 2, 3)
  println(typed)
  println(typed(2))
  println(typed.head)
  println(typed.tail)
  println(typed.length)

  val arr = Array(1, 2, 3, true, "string")
  // this is cool
  arr.foreach(println)
  println(arr.toList)

  // this is nice, adding at beginning with +: and at end with :+
  val arr2 = 0 +: arr :+ 4
  arr2.foreach(println)

  // concat
  val arr3 = arr ++ arr2
  println(arr3.toList)

  // can change the size of array
  val buff = ArrayBuffer(1, 2, 3)
  buff.addOne(5)
  buff.addAll(List(6, 7, 8))
  println(buff)
  buff.remove(0)
  println(buff)
  // this is pretty cool to remove elements from list/array
  buff --= List(3, 7, 8)
  println(buff)
  // sets are immutable
  val values = Set[Int](1, 2, 3, 2, 5, 1)
  println(values)

  // Hashset can be mutable or immutable
  val mutableSet = mutable.HashSet[Int](1, 2, 3, 2)
  mutableSet.add(5)
  mutableSet.remove(2)
  println(mutableSet)
  // add or remove
  mutableSet += 2
  mutableSet += (9, 10)
  mutableSet -= 5
  mutableSet -= (3, 1)
  println(mutableSet)
  mutableSet.addAll(List(5, 6, 7, 8, 7))
  println(mutableSet)
}
