package models

// simple user with id, name and permissions
final case class User(
                       id: Int,
                       name: String,
                       permissions: Permissions.Mask = Permissions.Mask.empty
                     ):

  def addPermission(perms: Permissions)(perm: Permissions.Mask): User =
    copy(permissions = perms.grant(permissions, perm))

  def removePermission(perms: Permissions)(perm: Permissions.Mask): User =
    copy(permissions = perms.revoke(permissions, perm))

  def setPermission(perms: Permissions)(perm: Permissions.Mask, enabled: Boolean): User =
    if enabled then addPermission(perms)(perm) else removePermission(perms)(perm)

  def hasPermission(perms: Permissions)(perm: Permissions.Mask): Boolean =
    perms.has(permissions, perm)

  def hasAllPermissions(perms: Permissions)(permMask: Permissions.Mask): Boolean =
    perms.hasAll(permissions, permMask)

  def hasAnyPermissions(perms: Permissions)(permMask: Permissions.Mask): Boolean =
    perms.hasAny(permissions, permMask)