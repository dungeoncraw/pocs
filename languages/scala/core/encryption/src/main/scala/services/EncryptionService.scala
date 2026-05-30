package services

import enums.FileShareError
import models.CypherPayload

import java.security.SecureRandom
import javax.crypto.{Cipher, KeyGenerator, SecretKey}
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}
import scala.util.Try

object EncryptionService:

  private val random = SecureRandom()

  private val Algorithm = "AES"
  private val Transformation = "AES/GCM/NoPadding"

  private val IvLengthBytes = 12
  private val TagLengthBits = 128

  def generateAes256Key(): SecretKey =
    val keyGenerator = KeyGenerator.getInstance(Algorithm)
    keyGenerator.init(256)
    keyGenerator.generateKey()

  def encrypt(
               plainBytes: Array[Byte],
               key: SecretKey
             ): Either[FileShareError, CypherPayload] =
    Try {
      val iv = new Array[Byte](IvLengthBytes)
      random.nextBytes(iv)

      val cipher = Cipher.getInstance(Transformation)
      val spec = GCMParameterSpec(TagLengthBits, iv)

      cipher.init(Cipher.ENCRYPT_MODE, key, spec)

      val cipherText = cipher.doFinal(plainBytes)

      CypherPayload(
        iv = iv,
        cipherText = cipherText
      )
    }.toEither.left.map(error =>
      FileShareError.CryptoFailure(error.getMessage)
    )

  def decrypt(
               payload: CypherPayload,
               key: SecretKey
             ): Either[FileShareError, Array[Byte]] =
    Try {
      val cipher = Cipher.getInstance(Transformation)
      val spec = GCMParameterSpec(TagLengthBits, payload.iv)

      cipher.init(Cipher.DECRYPT_MODE, key, spec)

      cipher.doFinal(payload.cipherText)
    }.toEither.left.map(error =>
      FileShareError.CryptoFailure(error.getMessage)
    )

  def encryptFileKey(
                      fileKey: SecretKey,
                      userKey: SecretKey
                    ): Either[FileShareError, CypherPayload] =
    encrypt(fileKey.getEncoded, userKey)

  def decryptFileKey(
                      encryptedFileKey: CypherPayload,
                      userKey: SecretKey
                    ): Either[FileShareError, SecretKey] =
    decrypt(encryptedFileKey, userKey).map { rawKey =>
      SecretKeySpec(rawKey, Algorithm)
    }