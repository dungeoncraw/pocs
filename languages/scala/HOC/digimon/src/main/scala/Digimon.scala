import enums.Stage

case class Digimon(name: String, stage: Stage, level: Int):
  def pretty: String = f"$name%-14s  ${stage.toString}%-11s  lvl=$level%2d"

object DigimonGame:
  private type Rule = Digimon => Digimon

  private def evolveIf(cond: Digimon => Boolean)(evolve: Digimon => Digimon): Rule =
    d => if cond(d) then evolve(d) else d

  private def applyToRoster(rule: Rule)(roster: List[Digimon]): List[Digimon] =
    roster.map(rule)

  private def applyRules(rules: List[Rule])(roster: List[Digimon]): List[Digimon] =
    rules.foldLeft(roster)((rs, rule) => applyToRoster(rule)(rs))

  def runTurns(turns: Int)(turn: List[Digimon] => List[Digimon])(start: List[Digimon]): List[Digimon] =
    Iterator.iterate(start)(turn).drop(turns).next()

  private val train: Rule =
    d => d.copy(level = d.level + 1)

  private val agumonToGreymon: Rule =
    evolveIf(d => d.name == "Agumon" && d.stage == Stage.Rookie && d.level >= 16) { d =>
      d.copy(name = "Greymon", stage = Stage.Champion)
    }

  private val gabumonToGarurumon: Rule =
    evolveIf(d => d.name == "Gabumon" && d.stage == Stage.Rookie && d.level >= 16) { d =>
      d.copy(name = "Garurumon", stage = Stage.Champion)
    }

  private val greymonToMetalGreymon: Rule =
    evolveIf(d => d.name == "Greymon" && d.stage == Stage.Champion && d.level >= 32) { d =>
      d.copy(name = "MetalGreymon", stage = Stage.Ultimate)
    }

  private val garurumonToWereGarurumon: Rule =
    evolveIf(d => d.name == "Garurumon" && d.stage == Stage.Champion && d.level >= 32) { d =>
      d.copy(name = "WereGarurumon", stage = Stage.Ultimate)
    }

  private val metalGreymonToWarGreymon: Rule =
    evolveIf(d => d.name == "MetalGreymon" && d.stage == Stage.Ultimate && d.level >= 48) { d =>
      d.copy(name = "WarGreymon", stage = Stage.Mega)
    }

  private val wereGarurumonToMetalGarurumon: Rule =
    evolveIf(d => d.name == "WereGarurumon" && d.stage == Stage.Ultimate && d.level >= 48) { d =>
      d.copy(name = "MetalGarurumon", stage = Stage.Mega)
    }

  val oneTurn: List[Digimon] => List[Digimon] =
    roster =>
      applyRules(
        List(
          train,
          agumonToGreymon,
          gabumonToGarurumon,
          greymonToMetalGreymon,
          garurumonToWereGarurumon,
          metalGreymonToWarGreymon,
          wereGarurumonToMetalGarurumon
        )
      )(roster)

  def printRoster(title: String, roster: List[Digimon]): Unit =
    println(title)
    roster.foreach(d => println(s"  - ${d.pretty}"))
    println()
