import models.{FileStorage, User}
import services.{EncryptionService, FileShareService}
import store.{FileAccessStore, InMemoryFileStore, MetadataStore, UserStore}
import types.UserId

import java.nio.charset.StandardCharsets.UTF_8

@main
def main(): Unit = {
  val (uStore, mStore, aStore, storage) = (UserStore(), MetadataStore(), FileAccessStore(), InMemoryFileStore())
  val share = FileShareService(uStore, mStore, aStore, storage)

  val anakin = User(UserId("anakin"), "Anakin", EncryptionService.generateAes256Key())
  val grivous = User(UserId("grivous"), "Grivous", EncryptionService.generateAes256Key())
  uStore.add(anakin); uStore.add(grivous)

  val fileId = share.saveFile(anakin.id, "report.txt", "Secret report".getBytes(UTF_8), Set("secret")).fold(e => sys.error(s"Save failed: $e"), id => id)
  println(s"Saved: ${fileId.value}")

  share.shareFile(anakin.id, fileId, grivous.id)
  println(s"Bob restored: ${share.restoreFile(grivous.id, fileId).map(f => new String(f.bytes, UTF_8))}")

  share.revokeAccess(anakin.id, fileId, grivous.id)
  println(s"Bob after revoke: ${share.restoreFile(grivous.id, fileId)}")

  share.deleteFile(anakin.id, fileId)
  println(s"Alice after delete: ${share.restoreFile(anakin.id, fileId)}")
}

