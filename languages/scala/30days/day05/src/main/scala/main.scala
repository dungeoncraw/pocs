

@main
def main(): Unit = {
  val user = Manager(1)
  getUserInfo(user)
  val employee = Employee(2)
  getUserInfo(employee)
  val guest = Guest(3)
  getUserInfo(guest)
}

def getUserInfo(user: User): Unit =
  user match
    // this is extremely useful for pattern matching, so bind the matched class to a variable
    case manager @ Manager(id) => {
      println(user.getRole)
      println(user.getAccess)
      println(manager.supervise(Employee(1)))
    }
    case employee @ Employee(id) => {
      println(user.getRole)
      println(user.getAccess)
      println(employee.reportTo(Manager(1)))
    }
    case guest @ Guest(id) => {
      println(user.getRole)
      println(user.getAccess)
    }
