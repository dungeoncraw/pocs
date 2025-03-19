import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine
import scala.util.Random

var board = ArrayBuffer[ArrayBuffer[String]]()

@main
def tictictoe(): Unit =
  for (i <- 0 to 2)
    val row = ArrayBuffer[String]()
    for(j <- 0 to 2)
      row.addOne("")
    board.addOne(row)
  printBoard()
  var continueGame = true
  while (continueGame)
    println("Enter your move (1-3): ")
    val input = readLine()
    var x = 0
    var y = 0
    try
      var skipRound = false
      val positions = input.split(",")
      x = positions(0).trim.toInt
      y = positions(1).trim.toInt

      if (board(x-1)(y-1) != "")
        println("Position already taken")
        skipRound = true
      else
        board(x-1)(y-1) = "X"
        printBoard()
      if (!skipRound)
        val playerWon = checkWinner(true)
        if (playerWon)
          println("\uD83C\uDF7E \uD83C\uDF8a \uD83C\uDF7E \uD83C\uDF7E\uD83C\uDF8a \uD83C\uDF7E\uD83C\uDF8a")
          println("Player won!")
          continueGame = false

          val boardFull = checkBoardFull()
          if (boardFull && !playerWon)
            println("Tie!")
            continueGame = false
        if(continueGame)
          placeComputerMove()
          printBoard()
          val computerWon = checkWinner(false)
          if (computerWon)
            println("Computer won!")
    catch
      case e: Exception => println("Invalid input")

def printBoard():Unit =
  println("-------------")
  for (i <- 0 to 2)
    for (j <- 0 to 2)
      board(i)(j) match
        case "X" => print("| X ")
        case "O" => print("| O ")
        case _ => print("|   ")
    println("|")
  println("-------------")

def randomPos() = Random.nextInt(3)

def placeComputerMove() =
  var x = randomPos()
  var y = randomPos()
  while (board(x)(y) != "")
    x = randomPos()
    y = randomPos()
  board(x)(y) = "O"

def checkWinner(player: Boolean): Boolean =
  var won = false
  val checkSymbols = if (player) "X" else "O"
  for(i <- 0 to 2)
    if (board(i)(0) == checkSymbols && board(i)(1) == checkSymbols && board(i)(2) == checkSymbols)
      won = true
    if (board(0)(i) == checkSymbols && board(1)(i) == checkSymbols && board(2)(i) == checkSymbols)
      won = true
    if (board(0)(0) == checkSymbols && board(1)(1) == checkSymbols && board(2)(2) == checkSymbols)
      won = true
    if(board(0)(2) == checkSymbols && board(1)(1) == checkSymbols && board(2)(0) == checkSymbols)
      won = true
  won

def checkBoardFull(): Boolean =
  var full = true
  for (i <- 0 to 2)
    for (j <- 0 to 2)
      if (board(i)(j) == "")
        full = false
  full