package validators

import engine.annotations.{GenerateValidator, Min, NotBlank, NotNull}
import engine.api.{ValidationResult, Validator}
import engine.rules.{NumberRules, StringRules}

@GenerateValidator
case class Address(
                    @NotBlank(message = "street is required")
                    street: String,
                    @NotNull(message = "number is required")
                    @Min(value = 1, message = "number must be positive")
                    number: Int,
                    @NotBlank(message = "city is required")
                    city: String
                  )

class AddressValidator extends Validator[Address]:
  override def validate(value: Address): ValidationResult =
    val result = new ValidationResult()
    if !StringRules.isNotBlank(value.street) then
      result.addError("street", "street is required", value.street)
    if !NumberRules.isMin(value.number.toLong, 1) then
      result.addError("number", "number must be positive", value.number)
    if !StringRules.isNotBlank(value.city) then
      result.addError("city", "city is required", value.city)
    result
