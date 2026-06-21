package engine.rules

object NumberRules:
  def isMin(value: Long, min: Long): Boolean =
    value >= min

  def isMax(value: Long, max: Long): Boolean =
    value <= max
