object FormValidation {

  type Validator = String => Either[String, String]

  def validateField(fieldName: String)(rule: String => Boolean)(errorMessage: String): Validator = {
    (value: String) =>
      if (rule(value)) Right(value)
      else Left(s"$fieldName: $errorMessage")
  }

  val notEmpty: String => Boolean = _.nonEmpty
  val minLength: Int => String => Boolean = min => _.length >= min
  val isEmail: String => Boolean = _.contains("@")

  val validateUsername = validateField("Username")(minLength(3))("must be at least 3 characters long")
  val validateEmail = validateField("Email")(isEmail)("must be a valid email address")

  def runExample(): Unit = {
    println("--- Multi-step Form Validation using Currying ---")

    val usernameInput = "al"
    val emailInput = "invalid-email"

    val usernameResult = validateUsername(usernameInput)
    println(s"Step 1 (Username '$usernameInput'): $usernameResult")

    val emailResult = validateEmail(emailInput)
    println(s"Step 2 (Email '$emailInput'): $emailResult")

    val validUsername = "alice"
    val validEmail = "alice@example.com"

    val finalResult = for {
      u <- validateUsername(validUsername)
      e <- validateEmail(validEmail)
    } yield (u, e)

    println(s"Final Result (Valid inputs): $finalResult")
  }
}
