package validators

import engine.annotations.{GenerateValidator, Length, Max, Min, NotBlank, NotNull, Valid}
import engine.api.{ValidationResult, Validator}
import engine.rules.{NumberRules, StringRules}

@GenerateValidator
case class UserRequest(
                        @NotNull(message = "id is required")
                        id: Long,
                        @NotBlank(message = "username is required")
                        @Length(min = 3, max = 20, message = "username must be between 3 and 20 characters")
                        username: String,
                        @NotNull(message = "age is required")
                        @Min(value = 18, message = "age must be at least 18")
                        @Max(value = 120, message = "age must be at most 120")
                        age: Int,
                        @Valid
                        address: Address
                      )

class UserRequestValidator extends Validator[UserRequest]:
  private val addressValidator = new AddressValidator()

  override def validate(value: UserRequest): ValidationResult =
    val result = new ValidationResult()

    if !StringRules.isNotBlank(value.username) then
      result.addError("username", "username is required", value.username)
    else if !StringRules.hasLength(value.username, 3, 20) then
      result.addError("username", "username must be between 3 and 20 characters", value.username)

    if !NumberRules.isMin(value.age.toLong, 18) then
      result.addError("age", "age must be at least 18", value.age)
    if !NumberRules.isMax(value.age.toLong, 120) then
      result.addError("age", "age must be at most 120", value.age)

    if value.address != null then
      val addressResult = addressValidator.validate(value.address)
      result.addNested("address", addressResult)

    result
