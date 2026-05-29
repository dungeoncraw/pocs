package enums

import types.{FileId, StoragePath, UserId}

enum FileShareError:
  case UserNotFound(userId: UserId)
  case FileNotFound(fileId: FileId)
  case AccessDenied(userId: UserId, fileId: FileId)
  case AlreadyDeleted(fileId: FileId)
  case StorageFileMissing(path: StoragePath)
  case CryptoFailure(message: String)
  case CannotRevokeOwner(fileId: FileId)