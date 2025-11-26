package com.tetokeguii.day20

// reusing the code from day10, as the focus is unit test
@main
def main(): Unit = {
  val stringBox = Box("Hello")
  val intBox = Box(42)
  val boolBox = Box(true)
  println(stringBox)
  println(intBox)
  println(boolBox)
  val result1 = identity(42)
  val result2 = identity("hello")
  val swapped = swap((1, "two"))
  println(swapped)
  println(result1)
  println(result2)
  val dogContainer = PetContainer[Dog](Dog())
  val catContainer = PetContainer[Cat](Cat())
  //  the next line fails as the generic type is not a subtype defined on upper bound
  //  val lionContainer = PetContainer[Lion](Lion())


  val dogProducer: Producer[Dog] = new Producer[Dog] {
    def get(): Dog = new Dog
  }
  // covariant of Producer
  val animalProducer: Producer[Animal] = dogProducer
  val animal: Animal = animalProducer.get()

  // List is covariant
  val dogs: List[Dog] = List(new Dog, new Dog)
  val animals: List[Animal] = dogs // ✓ Works

  // Vector is covariant
  val dogVector: Vector[Dog] = Vector(new Dog)
  val animalVector: Vector[Animal] = dogVector // ✓ Works

}



