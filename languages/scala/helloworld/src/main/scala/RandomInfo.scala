import scala.io.StdIn
import java.time.Year

@main
def random(): Unit = {
    println("What\'s your name?")
    val input = StdIn.readLine()
    println(s"Nice to meet you $input")

    println("Which year did you come to life?")
    val number = StdIn.readLine().toInt
    val diff = Year.now.getValue - number
    println(s"You have $diff years")

}