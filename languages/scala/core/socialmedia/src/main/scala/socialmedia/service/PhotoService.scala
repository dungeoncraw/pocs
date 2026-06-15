package socialmedia.service

import socialmedia.domain.*
import java.time.Instant

object PhotoService:

  def publishPhoto(authorId: UserId, url: String, caption: String): AppStateResult[Photo] =
    StateResult.get[AppState, AppError].flatMap { state =>
      if !state.users.contains(authorId) then
        StateResult.error(AppError.UserNotFound(authorId))
      else
        val photo = Photo(
          id = PhotoId.create(),
          authorId = authorId,
          url = url.trim,
          caption = caption.trim,
          taggedUsers = Set.empty,
          createdAt = Instant.now()
        )
        StateResult.modify[AppState, AppError](s =>
          s.copy(photos = s.photos + (photo.id -> photo))
        ).map(_ => photo)
    }

  def tagPhoto(photoId: PhotoId, requesterId: UserId, taggedUserId: UserId): AppStateResult[Unit] =
    StateResult.get[AppState, AppError].flatMap { state =>
      state.photos.get(photoId) match
        case None => StateResult.error(AppError.PhotoNotFound(photoId))
        case Some(photo) =>
          if !state.users.contains(requesterId) then StateResult.error(AppError.UserNotFound(requesterId))
          else if !state.users.contains(taggedUserId) then StateResult.error(AppError.UserNotFound(taggedUserId))
          else if photo.authorId != requesterId then StateResult.error(AppError.OnlyAuthorCanTagPhoto)
          else
            val updatedPhoto = photo.copy(taggedUsers = photo.taggedUsers + taggedUserId)
            StateResult.modify[AppState, AppError](s =>
              s.copy(photos = s.photos + (photoId -> updatedPhoto))
            )
    }

  def commentOnPhoto(photoId: PhotoId, authorId: UserId, text: String): AppStateResult[Comment] =
    val cleanText = text.trim
    if cleanText.isEmpty then StateResult.error(AppError.EmptyComment)
    else
      StateResult.get[AppState, AppError].flatMap { state =>
        if !state.photos.contains(photoId) then StateResult.error(AppError.PhotoNotFound(photoId))
        else if !state.users.contains(authorId) then StateResult.error(AppError.UserNotFound(authorId))
        else
          val comment = Comment(
            id = CommentId.create(),
            photoId = photoId,
            authorId = authorId,
            text = cleanText,
            createdAt = Instant.now()
          )
          val oldComments = state.comments.getOrElse(photoId, List.empty)
          StateResult.modify[AppState, AppError](s =>
            s.copy(comments = s.comments + (photoId -> (oldComments :+ comment)))
          ).map(_ => comment)
      }
