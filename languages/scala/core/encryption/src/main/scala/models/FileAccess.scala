package models

import enums.Permission
import types.{FileId, UserId}

final case class FileAccess(
                             fileId: FileId,
                             userId: UserId,
                             encryptedFileKey: CypherPayload,
                             permission: Permission
                           )