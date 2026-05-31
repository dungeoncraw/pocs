package models

import enums.Permission
import types.{FileId, UserId}

import java.time.Instant

final case class FileSummary(
                              fileId: FileId,
                              fileName: String,
                              ownerId: UserId,
                              sizeBytes: Long,
                              mimeType: String,
                              tags: Set[String],
                              permission: Permission,
                              createdAt: Instant
                            )