package engine.annotations

import scala.annotation.StaticAnnotation

case class GenerateValidator(className: String = "") extends StaticAnnotation

case class Constraint(validator: String) extends StaticAnnotation

case class NotNull(message: String = "must not be null") extends StaticAnnotation

case class NotBlank(message: String = "must not be blank") extends StaticAnnotation

case class Length(min: Int = 0, max: Int = Int.MaxValue, message: String = "length is invalid") extends StaticAnnotation

case class Min(value: Long, message: String = "value is too small") extends StaticAnnotation

case class Max(value: Long, message: String = "value is too large") extends StaticAnnotation

case class Valid() extends StaticAnnotation
