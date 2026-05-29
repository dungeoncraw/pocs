package models

final case class Cypher(
                                iv: Array[Byte],
                                cipherText: Array[Byte]
                              )