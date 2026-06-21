package engine.api

trait Validator[T]:
  def validate(value: T): ValidationResult
