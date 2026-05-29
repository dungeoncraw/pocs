package models

final case class RestoreFile(
                               fileName: String,
                               mimeType: String,
                               bytes: Array[Byte]
                             )