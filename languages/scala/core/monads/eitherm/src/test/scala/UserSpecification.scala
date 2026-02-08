import org.scalacheck.Properties
import org.scalacheck.Prop.{forAll, propBoolean}

object UserSpecification extends Properties("User"):

  property("makeUser creates a valid User when name is non-blank and age is non-negative") = 
    forAll { (name: String, age: Int) =>
      (name.trim.nonEmpty && age >= 0) ==> {
        val result = makeUser(name, age.toString)
        result.isRight && result.exists(_.name == name.trim) && result.exists(_.age == age)
      }
    }

  property("makeUser fails when name is empty or only whitespace") = 
    forAll(org.scalacheck.Gen.oneOf("", " ", "   ", "\t", "\n")) { (name: String) =>
      forAll { (age: Int) =>
        makeUser(name, age.toString).isLeft
      }
    }

  property("makeUser fails when age is not a valid integer") = 
    forAll { (name: String, ageStr: String) =>
      ageStr.toIntOption.isEmpty ==> {
        makeUser(name, ageStr).isLeft
      }
    }

  property("makeUser fails when age is negative") = 
    forAll { (name: String, age: Int) =>
      age < 0 ==> {
        makeUser(name, age.toString).isLeft
      }
    }
