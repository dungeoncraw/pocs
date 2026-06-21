package engine.rules

object StringRules:
  def isNotBlank(value: String): Boolean =
    value != null && value.trim.nonEmpty

  def hasLength(value: String, min: Int, max: Int): Boolean =
    if value == null then true
    else value.length >= min && value.length <= max
