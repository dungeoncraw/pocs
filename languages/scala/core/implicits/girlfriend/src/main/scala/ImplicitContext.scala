
case class GirlfriendPreferences(mood: String, likesSpicy: Boolean)

object ImplicitContext {

  private def suggestRestaurant(dish: String)(using prefs: GirlfriendPreferences): Unit = {
    if (prefs.likesSpicy) {
      println(s"Since she is in a ${prefs.mood} mood and likes spicy food, let's go for: $dish (Extra Hot!)")
    } else {
      println(s"She is in a ${prefs.mood} mood but prefers it mild, so $dish (No Spice) is perfect.")
    }
  }

  def run(): Unit = {
    println("\n--- Testing Implicit Context (Girlfriend Food Quest) ---")

    given spicyLover: GirlfriendPreferences = GirlfriendPreferences("adventurous", likesSpicy = true)

    suggestRestaurant("Thai Green Curry")

    {
      given pickyEater: GirlfriendPreferences = GirlfriendPreferences("calm", likesSpicy = false)
      suggestRestaurant("Italian Pasta")
    }
  }
}
