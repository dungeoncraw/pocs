package store

import models.User
import types.UserId

import scala.collection.mutable

final class UserStore:

  private val users = mutable.Map.empty[UserId, User]

  def add(user: User): Unit =
    users(user.id) = user

  def find(userId: UserId): Option[User] =
    users.get(userId)