
object Evidence {

  private sealed trait Cuisine
  private case object Italian extends Cuisine
  private case object Japanese extends Cuisine

  private def confirmCuisine[T](dish: T)(using ev: T <:< Cuisine): Unit = {
    println(s"Confirmed: '$dish' is indeed a known cuisine. Ordering now...")
  }

  private def handleMultipleOptions[T](options: T)(using ev: T <:< Iterable[String]): Unit = {
    val choices = ev(options)
    println(s"She gave you ${choices.size} options: ${choices.mkString(", ")}. Good luck picking one!")
  }

  def run(): Unit = {
    println("--- Testing Type Evidence (Cuisine Confirmation) ---")
    confirmCuisine(Italian)
    confirmCuisine(Japanese)
    // confirmCuisine("Pizza") // This would fail because String is not a Cuisine

    println("\n--- Testing Type Conformance (Handling Options) ---")
    handleMultipleOptions(List("Sushi", "Burger", "Tacos"))
    handleMultipleOptions(Set("Salad"))
    // handleMultipleOptions("Pizza") // This would fail because String is not an Iterable[String]
  }
}
