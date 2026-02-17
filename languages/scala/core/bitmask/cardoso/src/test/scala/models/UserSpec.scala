package models

import munit.FunSuite

class UserSpec extends FunSuite:
  import Permissions.*

  private val perms = Permissions()

  test("new User should have empty permissions by default"):
    val u = User(1, "alice")
    assertEquals(u.permissions.value, Mask.empty.value)

  test("addPermission should add the specified bit"):
    val u0 = User(1, "alice")
    val u1 = u0.addPermission(perms)(Perm.Read)
    assert(u1.hasPermission(perms)(Perm.Read))
    assert(!u1.hasPermission(perms)(Perm.Write))

  test("removePermission should clear the specified bit"):
    val u0 = User(1, "alice").addPermission(perms)(Perm.Read)
    val u1 = u0.removePermission(perms)(Perm.Read)
    assert(!u1.hasPermission(perms)(Perm.Read))

  test("setPermission(true) should grant and setPermission(false) should revoke"):
    val u0 = User(1, "alice")
    val u1 = u0.setPermission(perms)(Perm.Execute, true)
    val u2 = u1.setPermission(perms)(Perm.Execute, false)
    assert(u1.hasPermission(perms)(Perm.Execute))
    assert(!u2.hasPermission(perms)(Perm.Execute))

  test("hasAllPermissions across combined bits works"):
    val need = Perm.Read | Perm.Write
    val u = User(1, "bob")
      .addPermission(perms)(Perm.Read)
      .addPermission(perms)(Perm.Write)
    assert(u.hasAllPermissions(perms)(need))

  test("hasAllPermissions returns false when missing any bit"):
    val need = Perm.Read | Perm.Write
    val u = User(1, "bob").addPermission(perms)(Perm.Read)
    assert(!u.hasAllPermissions(perms)(need))

  test("hasAnyPermissions true when at least one matches and false otherwise"):
    val any = Perm.Read | Perm.Write
    val u = User(1, "carol").addPermission(perms)(Perm.Write)
    assert(u.hasAnyPermissions(perms)(any))
    val v = User(2, "dave").addPermission(perms)(Perm.Execute)
    assert(!v.hasAnyPermissions(perms)(any))
