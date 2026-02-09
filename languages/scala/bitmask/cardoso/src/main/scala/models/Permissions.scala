package models

final class Permissions:

  import Permissions.{Mask, Perm}

  def grant(mask: Mask, perm: Mask): Mask = mask | perm
  def revoke(mask: Mask, perm: Mask): Mask = mask & ~perm

  def has(mask: Mask, perm: Mask): Boolean =
    (mask & perm).value != 0

  def hasAll(mask: Mask, perms: Mask): Boolean =
    (mask & perms).value == perms.value

  def hasAny(mask: Mask, perms: Mask): Boolean =
    (mask & perms).value != 0

object Permissions:

  opaque type Mask = Int

  object Mask:
    def empty: Mask = 0

  extension (m: Mask)
    inline def value: Int = m
    infix inline def |(other: Mask): Mask = m | other
    infix inline def &(other: Mask): Mask = m & other
    inline def unary_~ : Mask = ~m

  object Perm:
    val Read: Mask = 1 << 0
    val Write: Mask = 1 << 1
    val Execute: Mask = 1 << 2
    val Admin: Mask = 1 << 3