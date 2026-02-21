
@main
def main(): Unit = {
  val p = Person("Alice", 30)

  // Use the Product API (available for all case classes)
  // productElementNames provides field names as an Iterator[String]
  // productIterator provides field values as an Iterator[Any]
  val labels = p.productElementNames.toList
  val values = p.productIterator.toList

  println(s"Class name: Person")
  println(s"Fields:")
  labels.zip(values).foreach { (label, value) =>
    println(s"  $label = $value")
  }
}

