@main
def main(): Unit = {

  enum Team:
    case Flamengo, Palmeiras, Corinthians, Vasco

  enum Standing:
    case Winner, RunnerUp, SemiFinalist, GroupStage

  final case class Tournament(name: String, standings: Map[Team, Standing])

  val cariocaCup = Tournament(
    name = "Tournament (BR Clubs)",
    standings = Map(
      Team.Flamengo    -> Standing.Winner,
      Team.Palmeiras   -> Standing.RunnerUp,
      Team.Corinthians -> Standing.SemiFinalist,
      Team.Vasco       -> Standing.Winner // intentionally missing
    )
  )
  val trophyMessage: PartialFunction[(Team, Standing), String] = {
    case (Team.Flamengo, Standing.Winner)      => "Flamengo lifted the trophy."
    case (Team.Palmeiras, Standing.Winner)     => "Palmeiras lifted the trophy."
    case (Team.Corinthians, Standing.Winner)   => "Corinthians lifted the trophy."
    // Intentionally missing:
    // case (Team.Vasco, Standing.Winner) => ...
    case (_, Standing.RunnerUp)                => "Finished 2nd: so close."
    case (_, Standing.SemiFinalist)            => "Reached the semis."
    case (_, Standing.GroupStage)              => "Out in the group stage."
  }

  println(s"=== ${cariocaCup.name} ===")

  // Safe usage: applyOrElse prevents MatchError
  cariocaCup.standings.toList.sortBy(_._1.toString).foreach { (team, standing) =>
    val msg = trophyMessage.applyOrElse(
      (team, standing),
      (_: (Team, Standing)) => s"[Not implemented] No message for $team as $standing."
    )
    println(f"$team%-12s -> $standing%-12s | $msg")
  }

  println()
  println("Defined for Vasco Winner? " + trophyMessage.isDefinedAt((Team.Vasco, Standing.Winner)))
  println("Defined for Flamengo Winner? " + trophyMessage.isDefinedAt((Team.Flamengo, Standing.Winner)))

  // Danger of partial functions, uncomment this
  // println(trophyMessage((Team.Vasco, Standing.Winner))) // would throw MatchError
}