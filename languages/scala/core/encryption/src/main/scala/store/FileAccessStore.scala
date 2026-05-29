package store

import models.FileAccess
import types.{FileId, UserId}

import scala.collection.mutable

final class FileAccessStore:

  private val accesses = mutable.Map.empty[(FileId, UserId), FileAccess]

  def grant(access: FileAccess): Unit =
    accesses((access.fileId, access.userId)) = access

  def revoke(fileId: FileId, userId: UserId): Unit =
    accesses.remove((fileId, userId))

  def findAccess(fileId: FileId, userId: UserId): Option[FileAccess] =
    accesses.get((fileId, userId))

  def listAccessibleFiles(userId: UserId): List[FileAccess] =
    accesses.values
      .filter(_.userId == userId)
      .toList