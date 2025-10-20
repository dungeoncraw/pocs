sealed trait User {
  def getRole: String = this.getClass.getSimpleName
  def getAccess: String
}

case class Manager(id: Int) extends User {
  def supervise(employee: Employee): String = s"Manager $id supervises $employee"
  def getAccess: String = "admin"
}
case class Employee(id: Int) extends User {
  def reportTo(manager: Manager): String = s"Employee $id reports to $manager"
  def getAccess: String = "read-write"
}
case class Guest(id: Int) extends User {
  def getAccess: String = "read-only"
}