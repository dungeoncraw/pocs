import validators.{Address, UserRequest, UserRequestValidator}

@main
def main(): Unit = {
  val validator = new UserRequestValidator()

  val validUser = UserRequest(1L, "john_doe", 25, Address("Main St", 123, "New York"))
  val result1 = validator.validate(validUser)
  println(s"Is Valid: ${result1.isValid}")

  val invalidUser = UserRequest(2L, "jd", 15, Address("First St", 10, "Los Angeles"))
  val result2 = validator.validate(invalidUser)
  println(s"Is Valid: ${result2.isValid}")
  result2.errors.foreach(e => println(s"  Error: ${e.path} -> ${e.message} (rejected: ${e.rejectedValue})"))

  val invalidAddress = UserRequest(3L, "bob_smith", 30, Address("", 0, ""))
  val result3 = validator.validate(invalidAddress)
  println(s"Is Valid: ${result3.isValid}")
  result3.errors.foreach(e => println(s"  Error: ${e.path} -> ${e.message} (rejected: ${e.rejectedValue})"))

  val tooOld = UserRequest(4L, "old_timer", 150, Address("Old St", 1, "Ancient City"))
  val result4 = validator.validate(tooOld)
  println(s"Is Valid: ${result4.isValid}")
  result4.errors.foreach(e => println(s"  Error: ${e.path} -> ${e.message} (rejected: ${e.rejectedValue})"))
}
