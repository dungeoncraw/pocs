package services

import enums.FileShareError
import models.CypherPayload

import java.nio.charset.StandardCharsets.UTF_8

class EncryptionServiceTest extends munit.FunSuite:

  test("generateAes256Key should generate a valid AES key") {
    val key = EncryptionService.generateAes256Key()

    assertEquals(key.getAlgorithm, "AES")
    assertEquals(key.getEncoded.length, 32) // 256 bits = 32 bytes
  }

  test("encrypt and decrypt should restore original bytes") {
    val key = EncryptionService.generateAes256Key()
    val originalText = "hello encrypted world"
    val originalBytes = originalText.getBytes(UTF_8)

    val result =
      for
        encrypted <- EncryptionService.encrypt(originalBytes, key)
        decrypted <- EncryptionService.decrypt(encrypted, key)
      yield decrypted

    assert(result.isRight)

    val decryptedBytes = result.toOption.get

    assert(decryptedBytes.sameElements(originalBytes))
    assertEquals(new String(decryptedBytes, UTF_8), originalText)
  }

  test("encrypt should produce different ciphertexts for same input because IV is random") {
    val key = EncryptionService.generateAes256Key()
    val originalBytes = "same content".getBytes(UTF_8)

    val encrypted1 = EncryptionService.encrypt(originalBytes, key).toOption.get
    val encrypted2 = EncryptionService.encrypt(originalBytes, key).toOption.get

    assert(!encrypted1.iv.sameElements(encrypted2.iv))
    assert(!encrypted1.cipherText.sameElements(encrypted2.cipherText))
  }

  test("decrypt should fail with wrong key") {
    val correctKey = EncryptionService.generateAes256Key()
    val wrongKey = EncryptionService.generateAes256Key()

    val originalBytes = "top secret".getBytes(UTF_8)

    val result =
      for
        encrypted <- EncryptionService.encrypt(originalBytes, correctKey)
        decrypted <- EncryptionService.decrypt(encrypted, wrongKey)
      yield decrypted

    assert(result.isLeft)

    result.left.foreach { error =>
      assert(error.isInstanceOf[FileShareError.CryptoFailure])
    }
  }

  test("encryptFileKey and decryptFileKey should restore original file key") {
    val fileKey = EncryptionService.generateAes256Key()
    val userKey = EncryptionService.generateAes256Key()

    val result =
      for
        encryptedFileKey <- EncryptionService.encryptFileKey(fileKey, userKey)
        decryptedFileKey <- EncryptionService.decryptFileKey(encryptedFileKey, userKey)
      yield decryptedFileKey

    assert(result.isRight)

    val decryptedFileKey = result.toOption.get

    assertEquals(decryptedFileKey.getAlgorithm, "AES")
    assert(decryptedFileKey.getEncoded.sameElements(fileKey.getEncoded))
  }

  test("decryptFileKey should fail with wrong user key") {
    val fileKey = EncryptionService.generateAes256Key()

    val correctUserKey = EncryptionService.generateAes256Key()
    val wrongUserKey = EncryptionService.generateAes256Key()

    val result =
      for
        encryptedFileKey <- EncryptionService.encryptFileKey(fileKey, correctUserKey)
        decryptedFileKey <- EncryptionService.decryptFileKey(encryptedFileKey, wrongUserKey)
      yield decryptedFileKey

    assert(result.isLeft)

    result.left.foreach { error =>
      assert(error.isInstanceOf[FileShareError.CryptoFailure])
    }
  }

  test("decrypt should fail when ciphertext is tampered") {
    val key = EncryptionService.generateAes256Key()
    val originalBytes = "do not modify me".getBytes(UTF_8)

    val encrypted = EncryptionService.encrypt(originalBytes, key).toOption.get

    val tamperedCipherText =
      encrypted.cipherText.clone()

    tamperedCipherText(0) =
      (tamperedCipherText(0) ^ 1).toByte

    val tamperedPayload = CypherPayload(
      iv = encrypted.iv,
      cipherText = tamperedCipherText
    )

    val result =
      EncryptionService.decrypt(tamperedPayload, key)

    assert(result.isLeft)

    result.left.foreach { error =>
      assert(error.isInstanceOf[FileShareError.CryptoFailure])
    }
  }

  test("decrypt should fail when IV is tampered") {
    val key = EncryptionService.generateAes256Key()
    val originalBytes = "important file".getBytes(UTF_8)

    val encrypted = EncryptionService.encrypt(originalBytes, key).toOption.get

    val tamperedIv =
      encrypted.iv.clone()

    tamperedIv(0) =
      (tamperedIv(0) ^ 1).toByte

    val tamperedPayload = CypherPayload(
      iv = tamperedIv,
      cipherText = encrypted.cipherText
    )

    val result =
      EncryptionService.decrypt(tamperedPayload, key)

    assert(result.isLeft)

    result.left.foreach { error =>
      assert(error.isInstanceOf[FileShareError.CryptoFailure])
    }
  }