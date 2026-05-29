package models

import types.{FileId, StoragePath, UserId}

import java.time.Instant

final case class Metadata(
                               fileId: FileId,
                               ownerId: UserId,
                               fileName: String,
                               tags: Set[String],
                               sizeBytes: Long,
                               mimeType: String,
                               storagePath: StoragePath,
                               createdAt: Instant,
                               updatedAt: Instant,
                               deletedAt: Option[Instant]
                             )