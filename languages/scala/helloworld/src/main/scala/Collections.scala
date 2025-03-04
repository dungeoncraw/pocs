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

  // maps
  val m1 = Map(1 -> "one", 2 -> "two")
  val m2 = Map(("k1", "one"), ("k2", "two"))
  val m3 = Map[Int, String](1 -> "one", 2 -> "two")
  val m4 = Map()
  println(m1(1))
  println(m2("k1"))
  println(m2.keySet)
  println(m2.values)
  println(m2.keys)

  val hm1 = mutable.HashMap(1 -> "one", 2 -> "two")
  hm1 += (3 -> "three")
  hm1.addOne(4 -> "four")
  println(hm1)

  hm1 ++= Map(5 -> "five", 6 -> "six")
  hm1.addAll(Map(7 -> "seven", 8 -> "eight"))
  hm1.remove(5)
  hm1 --= Set(3, 7, 8)
  println(hm1)
  println(hm1.size)
  println(hm1.contains(1))
  println(hm1.contains(3))
  println(hm1.head)
  println(hm1.tail)

  val t1 = (1, "one")
  val t2 = Tuple2(2, "two")
  val t3 = Tuple3[Int, String, Boolean](3, "three", true)
  println(t1(1))
  // this pattern _N get the element by position starting in 1, not by index like arrays
  println(t2._2)
  // modify value of element on copy
  val cont = t3.copy(_2 = "four")
  println(cont.productArity)

  // concat tuples and create a new one
  println(t1 ++ t2)

  // ranges
  // those include the last item of range
  val r1 = 1 to 10
  val r2 = 'a' to 'z'
  // those don't include the last item of range
  val r3 = 1 until 10
  val r4 = 'a' until 'z'

  // step
  val r5 = 1 to 10 by 2
  val r6 = 'a' to 'z' by 2

  // negative range
  val r7 = 10 to 1 by -1
  val r8 = (1 to 10).reverse

  for (i <- r8)
    println(i)
  // this is really nice
  for (row <- 1 to 5; column <- 3 to 9)
    println(s"x = $row, y = $column")

  // multiple filters
  for (r <- r1 if r % 2 == 0; if r > 5)
    println(r)
  // yield a value returned from FOR loop
  val output = for(v <- r7 if v % 2 == 0) yield v
  println(output)

  val items = 10
  var sum = 0
  while(sum < items) {
    sum += 1
    println(sum)
  }
}
