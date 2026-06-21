package engine.api

import scala.collection.mutable.ListBuffer

final class ValidationResult:
  private val _errors = ListBuffer.empty[ValidationError]

  def addError(path: String, message: String, rejectedValue: Any): Unit =
    _errors += ValidationError(path, message, rejectedValue)

  def addNested(prefix: String, nestedResult: ValidationResult): Unit =
    for error <- nestedResult.errors do
      addError(
        s"$prefix.${error.path}",
        error.message,
        error.rejectedValue
      )

  def isValid: Boolean = _errors.isEmpty

  def errors: List[ValidationError] = _errors.toList
