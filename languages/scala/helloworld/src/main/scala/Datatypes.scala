import scala.io.StdIn

@main
def datatypes(): Unit = {
    val cats = 3
    val longcats = cats.toLong
    println(cats.getClass)
    println(longcats.getClass)

    // issues when converting to smaller data type
    val people = 8_000_000_000l
    val fewPeople = people.toInt
    println(fewPeople)

    println("Enter a number")

    val input = StdIn.readLine()

    // converting number to some data types
    val myByte = input.toByte
    println(myByte)
    println(myByte.getClass)

    val myShort = input.toShort
    println(myShort)
    println(myShort.getClass)

    val myInt = input.toInt
    println(myInt)
    println(myInt.getClass)

    val myLong = input.toLong
    println(myLong)
    println(myLong.getClass)

    val myFloat = input.toFloat
    println(myFloat)
    println(myFloat.getClass)

    val myDouble = input.toDouble
    println(myDouble)
    println(myDouble.getClass)

    val pi = 3.14159

    val mpi = input.toLong * pi
    println(mpi.getClass)
    println(mpi)
}