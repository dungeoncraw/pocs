package com.tetokeguii.day20

import org.scalatest.funsuite.AnyFunSuite

// ScalaTest: tests for PetContainer and Animal/Pet basics
class PetContainerSpec extends AnyFunSuite {
  test("PetContainer holds a Dog and exposes it with correct type") {
    val c = PetContainer[Dog](new Dog)
    val p: Dog = c.pet
    assert(p.isInstanceOf[Dog])
    assert(p.name == "Dog")
  }

  test("PetContainer holds a Cat and exposes it with correct type") {
    val c = PetContainer[Cat](new Cat)
    val p: Cat = c.pet
    assert(p.isInstanceOf[Cat])
    assert(p.name == "Cat")
  }

  test("Animal names are returned correctly") {
    val a: Animal = new Lion
    assert(a.name == "Lion")
  }
}
