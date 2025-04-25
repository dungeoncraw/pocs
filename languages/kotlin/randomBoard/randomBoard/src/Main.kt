import java.util.Random
import java.util.Scanner


fun main() {
    val scanner = Scanner(System.`in`)
    println("Enter number of rows (x):")
    val x = scanner.nextInt()
    println("Enter number of columns (y):")
    val y = scanner.nextInt()

    val random = Random()
    val matrix = Array(x) { IntArray(y) { random.nextInt(-10, 11) } }

    println("Generated matrix:")
    for (row in matrix) {
        println(row.joinToString(" "))
    }
}