package validators

import engine.annotations.Constraint

case class Email(message: String = "must be a valid email") extends scala.annotation.StaticAnnotation

@Constraint(validator = "EmailValidator")
case class CustomEmail(@Email message: String = "must be a valid email") extends scala.annotation.StaticAnnotation
