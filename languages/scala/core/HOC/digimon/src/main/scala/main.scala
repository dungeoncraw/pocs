import DigimonGame.*
import enums.Stage

@main
def main(): Unit =

  val roster0 =
    List(
      Digimon("Agumon", Stage.Rookie, level = 15),
      Digimon("Gabumon", Stage.Rookie, level = 14),
      Digimon("Greymon", Stage.Champion, level = 31)
    )

  printRoster("Turn 0 (start):", roster0)

  val checkpoints = List(1, 2, 20, 40)
  checkpoints.foreach { n =>
    val rosterN = runTurns(n)(oneTurn)(roster0)
    printRoster(s"After $n turns:", rosterN)
  }