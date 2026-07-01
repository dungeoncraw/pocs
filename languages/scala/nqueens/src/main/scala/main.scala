
def queens(n: Int): List[List[(Int, Int)]] = {
  def placeQueens(k: Int): List[List[(Int, Int)]] =
    if (k == 0)
      List(List())
    else
      for {
        queens <- placeQueens(k - 1)
        column <- 1 to n
        row = k
        queen = (row, column)
        if isSafe(queen, queens)
      } yield queen :: queens
  placeQueens(n)
}

def isSafe(queen: (Int, Int), queens: List[(Int, Int)]): Boolean =
  queens forall (q => !inCheck(queen, q))
def inCheck(q1: (Int, Int), q2: (Int, Int)): Boolean =
  q1._1 == q2._1 || // same row
    q1._2 == q2._2 || // same column
    (q1._1 - q2._1).abs == (q1._2 - q2._2).abs // on diagonal
@main
def main(): Unit = {
  println(queens(4).length) //  2 solutions
  println(queens(8).length) //  92 solutions
  println(queens(12).length) // 14200 solutions
  println(queens(16).length) // 1477251365307979600 solutions broke Java heap space
}

