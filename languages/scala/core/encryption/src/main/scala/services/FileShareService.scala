package services

import enums.{FileShareError, Permission}
import models.{FileAccess, FileSummary, FileStorage, Metadata, RestoredFile}
import store.{FileAccessStore, MetadataStore, UserStore}
import types.{FileId, StoragePath, UserId}

import java.time.Instant
import java.util.UUID

type Result[A] = Either[FileShareError, A]

final class FileShareService(
                              userStore: UserStore,
                              metadataStore: MetadataStore,
                              accessStore: FileAccessStore,
                              storage: FileStorage
                            ):

  private def findUser(userId: UserId): Result[models.User] = userStore.find(userId).toRight(FileShareError.UserNotFound(userId))
  private def findMetadata(fileId: FileId): Result[Metadata] = metadataStore.find(fileId).toRight(FileShareError.FileNotFound(fileId))
  private def checkNotDeleted(metadata: Metadata): Result[Unit] = Either.cond(metadata.deletedAt.isEmpty, (), FileShareError.AlreadyDeleted(metadata.fileId))
  private def checkOwner(metadata: Metadata, userId: UserId): Result[Unit] = Either.cond(metadata.ownerId == userId, (), FileShareError.AccessDenied(userId, metadata.fileId))

  def saveFile(ownerId: UserId, fileName: String, bytes: Array[Byte], tags: Set[String], mimeType: String = "application/octet-stream"): Result[FileId] =
    for
      owner <- findUser(ownerId)
      fileKey = EncryptionService.generateAes256Key()
      encFile <- EncryptionService.encrypt(bytes, fileKey)
      encKey <- EncryptionService.encryptFileKey(fileKey, owner.encryptionKey)
    yield
      val fileId = FileId(UUID.randomUUID().toString)
      val path = StoragePath(s"files/${fileId.value}")
      val now = Instant.now()
      storage.put(path, encFile)
      metadataStore.save(Metadata(fileId, ownerId, fileName, tags.map(_.toLowerCase), bytes.length.toLong, mimeType, path, now, now, None))
      accessStore.grant(FileAccess(fileId, ownerId, encKey, Permission.Owner))
      fileId

  def restoreFile(userId: UserId, fileId: FileId): Result[RestoredFile] =
    for
      m <- findMetadata(fileId)
      _ <- checkNotDeleted(m)
      u <- findUser(userId)
      a <- accessStore.findAccess(fileId, userId).toRight(FileShareError.AccessDenied(userId, fileId))
      encFile <- storage.get(m.storagePath).toRight(FileShareError.StorageFileMissing(m.storagePath))
      fileKey <- EncryptionService.decryptFileKey(a.encryptedFileKey, u.encryptionKey)
      plainBytes <- EncryptionService.decrypt(encFile, fileKey)
    yield RestoredFile(m.fileName, m.mimeType, plainBytes)

  def deleteFile(userId: UserId, fileId: FileId): Result[Unit] =
    for m <- findMetadata(fileId); _ <- checkOwner(m, userId); _ <- checkNotDeleted(m)
    yield metadataStore.update(m.copy(updatedAt = Instant.now(), deletedAt = Some(Instant.now())))

  private def listFiles(userId: UserId): Result[List[FileSummary]] =
    findUser(userId).map(_ =>
      for
        a <- accessStore.listAccessibleFiles(userId)
        m <- metadataStore.find(a.fileId).toList if m.deletedAt.isEmpty
      yield FileSummary(m.fileId, m.fileName, m.ownerId, m.sizeBytes, m.mimeType, m.tags, a.permission, m.createdAt)
    )

  def searchFiles(userId: UserId, query: String): Result[List[FileSummary]] =
    val q = query.trim.toLowerCase
    listFiles(userId).map(_.filter(f => f.fileName.toLowerCase.contains(q) || f.mimeType.toLowerCase.contains(q) || f.tags.exists(_.contains(q))))

  def shareFile(ownerId: UserId, fileId: FileId, targetUserId: UserId, permission: Permission = Permission.Read): Result[Unit] =
    for
      m <- findMetadata(fileId)
      _ <- checkOwner(m, ownerId)
      o <- findUser(ownerId)
      t <- findUser(targetUserId)
      oa <- accessStore.findAccess(fileId, ownerId).toRight(FileShareError.AccessDenied(ownerId, fileId))
      fk <- EncryptionService.decryptFileKey(oa.encryptedFileKey, o.encryptionKey)
      ek <- EncryptionService.encryptFileKey(fk, t.encryptionKey)
    yield accessStore.grant(FileAccess(fileId, targetUserId, ek, permission))

  def revokeAccess(ownerId: UserId, fileId: FileId, targetUserId: UserId): Result[Unit] =
    for m <- findMetadata(fileId); _ <- checkOwner(m, ownerId); _ <- Either.cond(targetUserId != ownerId, (), FileShareError.CannotRevokeOwner(fileId))
    yield accessStore.revoke(fileId, targetUserId)