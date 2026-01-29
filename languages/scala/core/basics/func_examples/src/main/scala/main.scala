package com.dungeoncraw.func_examples

sealed trait Resource:
  def id: String
  def name: String

trait Encryptable:
  def encrypt(data: String): String = s"ENCRYPTED($data)"

trait Loggable:
  def log(message: String): Unit = println(s"[LOG] $message")

trait Hashable:
  def hash(data: String): String

case class UserProfile(id: String, name: String, email: String) extends Resource with Encryptable with Hashable:
  def hash(data: String): String = data.hashCode.toString

case class SystemConfig(id: String, name: String, value: String) extends Resource with Loggable with Hashable:
  def hash(data: String): String = data.hashCode.toString

case object GlobalRegistry extends Resource:
  val id = "ROOT_001"
  val name = "Global Resource Registry"

class ResourceManager(managerName: String):
  private var accessCount = 0

  def process(resource: Resource): Unit =
    accessCount += 1
    println(s"$managerName is processing ${resource.name} (Access #$accessCount)")
    resource match
      case u: UserProfile => 
        println(s"Securing user data for ${u.email}: ${u.encrypt(u.name)}")
      case c: SystemConfig => 
        c.log(s"Updating config: ${c.name}")
      case GlobalRegistry => 
        println("Accessing the immutable global registry")

object AuditService:
  val version = "2.1.0"
  
  def generateReport(resources: List[Resource]): Unit =
    println(s"--- Audit Report (v$version) ---")
    resources.foreach: r =>
      println(s"Verified: ${r.id} | Name: ${r.name}")

@main
def runSystem(): Unit =
  val user = UserProfile("USR-101", "Alice Smith", "alice@example.com")
  val config = SystemConfig("CFG-99", "MaxRetries", "5")
  val admin = ResourceManager("SystemAdmin")

  val items = List(user, config, GlobalRegistry)

  admin.process(user)
  admin.process(config)
  admin.process(GlobalRegistry)

  AuditService.generateReport(items)

  // Demonstrating a higher-order function/anonymous function
  val names = items.map(_.name.toUpperCase)
  println(s"All resource names: ${names.mkString(", ")}")

  println(s"Hash data for ${user.name}: ${user.hash(user.name)}")
  println(s"Hash data for ${config.name}: ${config.hash(config.name)}")