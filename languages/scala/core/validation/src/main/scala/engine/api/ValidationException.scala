package engine.api

class ValidationException(val result: ValidationResult)
    extends RuntimeException(s"Validation failed with ${result.errors.size} errors")
