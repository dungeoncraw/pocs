import models.{Permissions, User}

@main
def main(): Unit = {
  val perms = Permissions()
  val alice0 = User(1, "alice")

  val alice1 = alice0.addPermission(perms)(Permissions.Perm.Read)
  val alice2 = alice1.addPermission(perms)(Permissions.Perm.Execute)

  println(alice2.hasPermission(perms)(Permissions.Perm.Read))

  val needsReadAndExecute = Permissions.Perm.Read | Permissions.Perm.Execute
  println(alice2.hasAllPermissions(perms)(needsReadAndExecute))

  val alice3 = alice2.removePermission(perms)(Permissions.Perm.Read)
  println(alice3.hasPermission(perms)(Permissions.Perm.Read))
}