package com.dungeoncraw.battleship

import scala.util.Random

type Coordinate = (Int, Int)
type Ship = List[Coordinate]
type Board = Set[Coordinate]

@main
def main(): Unit = {
  val boardSize = Random.nextInt(10) + 5
  val numShips = Random.nextInt(3) + 1
  val shipSize = Random.nextInt(boardSize - 1) + 2

  println("--- Battleship Simulation ---")
  val ships = placeShips(numShips, shipSize, boardSize)
  var allShipParts = ships.flatten.toSet
  var hits = Set.empty[Coordinate]
  var misses = Set.empty[Coordinate]
  
  println(s"Ships placed: $numShips ships of size $shipSize on a ${boardSize}x${boardSize} board.")

  var attempts = 0
  val maxAttempts = 20

  while (allShipParts.nonEmpty && attempts < maxAttempts) {
    attempts += 1
    val target: Coordinate = (Random.nextInt(boardSize), Random.nextInt(boardSize))
    
    if (hits.contains(target) || misses.contains(target)) {
      attempts -= 1
    } else {
      if (allShipParts.contains(target)) {
        println(s"Attempt $attempts: Firing at $target... HIT!")
        hits += target
        allShipParts -= target
      } else {
        println(s"Attempt $attempts: Firing at $target... Miss.")
        misses += target
      }
    }
  }

  if (allShipParts.isEmpty) {
    println(s"\nVictory! All ships sunk in $attempts attempts.")
  } else {
    println(s"\nGame Over. You ran out of ammo. Remaining ship parts: $allShipParts")
  }
}

def placeShips(numShips: Int, shipSize: Int, boardSize: Int): List[Ship] = {
  var ships = List.empty[Ship]
  var occupied = Set.empty[Coordinate]

  while (ships.length < numShips) {
    val isHorizontal = Random.nextBoolean()
    val ship: Ship = if (isHorizontal) {
      val row = Random.nextInt(boardSize)
      val colStart = Random.nextInt(boardSize - shipSize + 1)
      (0 until shipSize).map(i => (row, colStart + i)).toList
    } else {
      val col = Random.nextInt(boardSize)
      val rowStart = Random.nextInt(boardSize - shipSize + 1)
      (0 until shipSize).map(i => (rowStart + i, col)).toList
    }

    if (ship.forall(c => !occupied.contains(c))) {
      ships = ship :: ships
      occupied ++= ship
    }
  }
  ships
}

