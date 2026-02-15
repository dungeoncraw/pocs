import munit.FunSuite

class FormValidationSpec extends FunSuite {

  test("notEmpty should validate non-empty strings") {
    assert(FormValidation.notEmpty("hello"))
    assert(!FormValidation.notEmpty(""))
  }

  test("minLength should validate string length") {
    val min3 = FormValidation.minLength(3)
    assert(min3("abc"))
    assert(min3("abcd"))
    assert(!min3("ab"))
  }

  test("isEmail should validate presence of @") {
    assert(FormValidation.isEmail("test@example.com"))
    assert(!FormValidation.isEmail("testexample.com"))
  }

  test("validateField should return Right for valid input") {
    val validator = FormValidation.validateField("Field")(_ == "valid")("error")
    assertEquals(validator("valid"), Right("valid"))
  }

  test("validateField should return Left for invalid input") {
    val validator = FormValidation.validateField("Field")(_ == "valid")("error")
    assertEquals(validator("invalid"), Left("Field: error"))
  }

  test("validateUsername should validate correctly") {
    assertEquals(FormValidation.validateUsername("alice"), Right("alice"))
    assertEquals(FormValidation.validateUsername("al"), Left("Username: must be at least 3 characters long"))
  }

  test("validateEmail should validate correctly") {
    assertEquals(FormValidation.validateEmail("alice@example.com"), Right("alice@example.com"))
    assertEquals(FormValidation.validateEmail("invalid-email"), Left("Email: must be a valid email address"))
  }
}
