fun main() {
    val fenwick = FenwickTree(9)

    fenwick.add(1, 1)
    fenwick.add(2, 2)
    fenwick.add(3, 3)
    fenwick.add(4, 4)
    fenwick.add(5, 5)
    fenwick.add(6, 6)
    fenwick.add(7, 7)
    fenwick.add(8, 8)
    fenwick.add(9, 9)

    println("sum(1) = ${fenwick.sum(1)}")   // 1
    println("sum(2) = ${fenwick.sum(2)}")   // 3
    println("sum(3) = ${fenwick.sum(3)}")   // 6
    println("sum(4) = ${fenwick.sum(4)}")   // 10
    println("sum(5) = ${fenwick.sum(5)}")   // 15
    println("sum(6) = ${fenwick.sum(6)}")   // 21
    println("sum(7) = ${fenwick.sum(7)}")   // 28
    println("sum(8) = ${fenwick.sum(8)}")   // 36
    println("sum(9) = ${fenwick.sum(9)}")   // 45

    println("rangeSum(1, 9) = ${fenwick.rangeSum(1, 9)}") // 45
    println("rangeSum(3, 7) = ${fenwick.rangeSum(3, 7)}") // 25
    println("rangeSum(5, 5) = ${fenwick.rangeSum(5, 5)}") // 5
}