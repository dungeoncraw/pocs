package models

import munit.FunSuite

class PermissionsSpec extends FunSuite:
  import Permissions.*

  private val perms = Permissions()

  test("Mask.empty should be zero"):
    assertEquals(Mask.empty.value, 0)

  test("grant should set a single permission bit"):
    val m0 = Mask.empty
    val m1 = perms.grant(m0, Perm.Read)
    assert(perms.has(m1, Perm.Read))
    assert(!perms.has(m1, Perm.Write))

  test("grant can combine multiple permissions with | operator"):
    val m = perms.grant(Mask.empty, Perm.Read | Perm.Execute)
    assert(perms.has(m, Perm.Read))
    assert(perms.has(m, Perm.Execute))
    assert(!perms.has(m, Perm.Write))

  test("double grant is idempotent (no duplicate bits)"):
    val m1 = perms.grant(Mask.empty, Perm.Read)
    val m2 = perms.grant(m1, Perm.Read)
    assertEquals(m1.value, m2.value)

  test("revoke should clear a single permission bit"):
    val m1 = perms.grant(Mask.empty, Perm.Read | Perm.Write)
    val m2 = perms.revoke(m1, Perm.Read)
    assert(!perms.has(m2, Perm.Read))
    assert(perms.has(m2, Perm.Write))

  test("double revoke leaves mask unchanged when bit already cleared"):
    val m1 = perms.grant(Mask.empty, Perm.Read)
    val m2 = perms.revoke(m1, Perm.Read)
    val m3 = perms.revoke(m2, Perm.Read)
    assertEquals(m2.value, m3.value)

  test("has returns false when permission not present"):
    val m = perms.grant(Mask.empty, Perm.Read)
    assert(!perms.has(m, Perm.Execute))

  test("hasAll true when mask contains every bit in required mask"):
    val m = perms.grant(Mask.empty, Perm.Read | Perm.Execute)
    val required = Perm.Read | Perm.Execute
    assert(perms.hasAll(m, required))

  test("hasAll false when at least one required bit is missing"):
    val m = perms.grant(Mask.empty, Perm.Read)
    val required = Perm.Read | Perm.Write
    assert(!perms.hasAll(m, required))

  test("hasAll with empty required mask should be true"):
    val m = perms.grant(Mask.empty, Perm.Read)
    assert(perms.hasAll(m, Mask.empty))

  test("hasAny true when at least one bit overlaps"):
    val m = perms.grant(Mask.empty, Perm.Read | Perm.Execute)
    val anyMask = Perm.Write | Perm.Execute
    assert(perms.hasAny(m, anyMask))

  test("hasAny false when no bits overlap or required is empty"):
    val m = perms.grant(Mask.empty, Perm.Read)
    assert(!perms.hasAny(m, Perm.Write))
    assert(!perms.hasAny(m, Mask.empty))
