package com.tetokeguii.day20

// MUnit tests for PetContainer and Producer covariance basics
class PetContainerSuiteMUnit extends munit.FunSuite {
  test("PetContainer exposes pet with correct type and name - Dog") {
    val c = PetContainer[Dog](new Dog)
    val p: Dog = c.pet
    assert(p.isInstanceOf[Dog])
    assertEquals(p.name, "Dog")
  }

  test("PetContainer exposes pet with correct type and name - Cat") {
    val c = PetContainer[Cat](new Cat)
    val p: Cat = c.pet
    assert(p.isInstanceOf[Cat])
    assertEquals(p.name, "Cat")
  }

  test("Producer covariance allows Dog producer to be used as Animal producer") {
    val dogProducer: Producer[Dog] = new Producer[Dog] {
      def get(): Dog = new Dog
    }
    val animalProducer: Producer[Animal] = dogProducer // covariance
    val a: Animal = animalProducer.get()
    assert(a.isInstanceOf[Dog])
    assertEquals(a.name, "Dog")
  }
}
