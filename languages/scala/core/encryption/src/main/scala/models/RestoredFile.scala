package models

final case class RestoredFile(
                                fileName: String,
                                mimeType: String,
                                bytes: Array[Byte]
                              )