package socialmedia.domain

import java.util.UUID
import java.time.Instant

opaque type UserId = UUID
object UserId:
  def apply(value: UUID): UserId = value
  def create(): UserId = UUID.randomUUID()
  extension (id: UserId) def value: UUID = id

opaque type PhotoId = UUID
object PhotoId:
  def apply(value: UUID): PhotoId = value
  def create(): PhotoId = UUID.randomUUID()
  extension (id: PhotoId) def value: UUID = id

opaque type CommentId = UUID
object CommentId:
  def apply(value: UUID): CommentId = value
  def create(): CommentId = UUID.randomUUID()
  extension (id: CommentId) def value: UUID = id

final case class User(
  id: UserId,
  username: String,
  name: String,
  createdAt: Instant
)

final case class Photo(
  id: PhotoId,
  authorId: UserId,
  url: String,
  caption: String,
  taggedUsers: Set[UserId],
  createdAt: Instant
)

final case class Comment(
  id: CommentId,
  photoId: PhotoId,
  authorId: UserId,
  text: String,
  createdAt: Instant
)

final case class Follow(
  followerId: UserId,
  followedId: UserId
)

final case class TimelinePost(
  photo: Photo,
  author: User,
  tags: List[User],
  comments: List[Comment]
)

final case class AppState(
  users: Map[UserId, User],
  photos: Map[PhotoId, Photo],
  comments: Map[PhotoId, List[Comment]],
  follows: Set[Follow]
)

object AppState:
  val empty: AppState = AppState(
    users = Map.empty,
    photos = Map.empty,
    comments = Map.empty,
    follows = Set.empty
  )
