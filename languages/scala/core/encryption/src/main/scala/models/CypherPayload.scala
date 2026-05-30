package models

final case class CypherPayload(
                                iv: Array[Byte],
                                cipherText: Array[Byte]
                              )
