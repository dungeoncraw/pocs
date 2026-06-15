package socialmedia.domain

enum AppError:
  case UserNotFound(id: UserId)
  case PhotoNotFound(id: PhotoId)
  case UsernameAlreadyExists(username: String)
  case CannotFollowYourself
  case EmptyComment
  case OnlyAuthorCanTagPhoto

  def message: String =
    this match
      case UserNotFound(id) => s"User not found: ${id.value}"
      case PhotoNotFound(id) => s"Photo not found: ${id.value}"
      case UsernameAlreadyExists(username) => s"Username already exists: $username"
      case CannotFollowYourself => "A user cannot follow themselves"
      case EmptyComment => "Comment cannot be empty"
      case OnlyAuthorCanTagPhoto => "Only the photo author can tag users"

type AppResult[A] = Either[AppError, A]
