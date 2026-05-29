package models

import types.UserId

import javax.crypto.SecretKey

final case class User(
                       id: UserId,
                       name: String,
                       encryptionKey: SecretKey
                     )