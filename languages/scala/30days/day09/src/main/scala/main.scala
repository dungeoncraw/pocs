
import java.time.LocalDateTime
import scala.collection.mutable

trait Auditing {
  def logOperation(operation: String, user: String): Unit = {
    val timestamp = LocalDateTime.now()
    println(s"[AUDIT] $timestamp - User: $user - Operation: $operation")
  }

  def logAccess(resource: String, user: String): Unit = {
    logOperation(s"Access to resource: $resource", user)
  }
}

trait Cache {
  private val cache: mutable.Map[String, Any] = mutable.Map()

  def getFromCache[T](key: String): Option[T] = {
    cache.get(key).asInstanceOf[Option[T]]
  }

  def saveToCache[T](key: String, value: T): Unit = {
    cache.put(key, value)
    println(s"[CACHE] Value saved with key: $key")
  }

  def removeFromCache(key: String): Unit = {
    cache.remove(key)
    println(s"[CACHE] Value removed with key: $key")
  }

  def clearCache(): Unit = {
    cache.clear()
    println("[CACHE] Cache cleared")
  }

  def cacheSize: Int = cache.size
}

class UserService extends Auditing with Cache {

  def findUser(id: String, loggedUser: String): Option[String] = {
    logAccess(s"user/$id", loggedUser)

    getFromCache[String](s"user_$id") match {
      case Some(user) =>
        println(s"[CACHE] User found: $user")
        Some(user)
      case None =>
        val user = s"User_$id"
        println(s"[DB] Fetching user: $user")

        saveToCache(s"user_$id", user)
        logOperation(s"User lookup ID: $id", loggedUser)

        Some(user)
    }
  }

  def updateUser(id: String, newName: String, loggedUser: String): Unit = {
    logOperation(s"User update ID: $id to name: $newName", loggedUser)

    removeFromCache(s"user_$id")

    println(s"[DB] User $id updated to: $newName")

    saveToCache(s"user_$id", newName)
  }

  def deleteUser(id: String, loggedUser: String): Unit = {
    logOperation(s"User deletion ID: $id", loggedUser)
    removeFromCache(s"user_$id")
    println(s"[DB] User $id deleted")
  }
}

@main
def main(): Unit = {
  val service = new UserService()

  println("\n1. First lookup (not in cache):")
  service.findUser("123", "admin")

  println("\n2. Second lookup (should come from cache):")
  service.findUser("123", "admin")

  println("\n3. Updating user:")
  service.updateUser("123", "John Smith", "admin")

  println("\n4. Lookup after update:")
  service.findUser("123", "admin")

  println("\n5. Deleting user:")
  service.deleteUser("123", "admin")

  println(s"\n6. Cache size: ${service.cacheSize}")

  println("\n7. Clearing cache:")
  service.clearCache()

  println(s"8. Cache size after clearing: ${service.cacheSize}")
}