package socialmedia.service

import socialmedia.domain.*
import java.time.Instant

object UserService:

  def createUser(username: String, name: String): AppStateResult[User] =
    val normalizedUsername = username.trim.toLowerCase
    StateResult.get[AppState, AppError].flatMap { state =>
      if state.users.values.exists(_.username == normalizedUsername) then
        StateResult.error(AppError.UsernameAlreadyExists(normalizedUsername))
      else
        val user = User(
          id = UserId.create(),
          username = normalizedUsername,
          name = name.trim,
          createdAt = Instant.now()
        )
        StateResult.modify[AppState, AppError](s =>
          s.copy(users = s.users + (user.id -> user))
        ).map(_ => user)
    }

  def follow(followerId: UserId, followedId: UserId): AppStateResult[Unit] =
    if followerId == followedId then StateResult.error(AppError.CannotFollowYourself)
    else
      StateResult.get[AppState, AppError].flatMap { state =>
        if !state.users.contains(followerId) then
          StateResult.error(AppError.UserNotFound(followerId))
        else if !state.users.contains(followedId) then
          StateResult.error(AppError.UserNotFound(followedId))
        else
          StateResult.modify[AppState, AppError](s =>
            s.copy(follows = s.follows + Follow(followerId, followedId))
          )
      }
