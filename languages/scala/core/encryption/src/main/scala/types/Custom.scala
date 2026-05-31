package types

opaque type UserId = String
object UserId:
  def apply(value: String): UserId = value
  extension (id: UserId) def value: String = id

opaque type FileId = String
object FileId:
  def apply(value: String): FileId = value
  extension (id: FileId) def value: String = id

opaque type StoragePath = String
object StoragePath:
  def apply(value: String): StoragePath = value
  extension (p: StoragePath) def value: String = p