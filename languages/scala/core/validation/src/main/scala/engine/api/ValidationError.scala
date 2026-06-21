package engine.api

case class ValidationError(
    path: String,
    message: String,
    rejectedValue: Any
)
