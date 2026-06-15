package socialmedia.service

import socialmedia.domain.*

object TimelineService:

  def timelineFor(userId: UserId): AppStateResult[List[TimelinePost]] =
    StateResult.inspect[AppState, AppError, Either[AppError, List[TimelinePost]]] { state =>
      if !state.users.contains(userId) then
        Left(AppError.UserNotFound(userId))
      else
        val followedUserIds = state.follows
          .filter(_.followerId == userId)
          .map(_.followedId)

        val visibleAuthorIds = followedUserIds + userId

        val photosFromFollowedUsers = state.photos.values
          .filter(photo => visibleAuthorIds.contains(photo.authorId))

        val photosWhereUserIsTagged = state.photos.values
          .filter(photo => photo.taggedUsers.contains(userId))

        val visiblePhotos = (photosFromFollowedUsers ++ photosWhereUserIsTagged)
          .toList
          .distinctBy(_.id)
          .sortBy(_.createdAt.toEpochMilli)
          .reverse

        val posts = visiblePhotos.flatMap { photo =>
          state.users.get(photo.authorId).map { author =>
            val taggedUsers = photo.taggedUsers.flatMap(state.users.get).toList
            val comments = state.comments.getOrElse(photo.id, List.empty)
            TimelinePost(photo, author, taggedUsers, comments)
          }
        }
        Right(posts)
    }.flatMap {
      case Left(err) => StateResult.error(err)
      case Right(posts) => StateResult.pure(posts)
    }
