package db

import scala.collection.mutable

object RedisDatabase:
  val instance = new RedisDatabase()

final class RedisDatabase:

  private val data =
    mutable.Map.empty[String, RedisEntry]

  private val lock = new Object

  def setString(
                 key: String,
                 value: Array[Byte]
               ): Unit =
    lock.synchronized {
      data.update(
        key,
        RedisEntry(
          value = RedisString(value.clone()),
          expiresAtMillis = None
        )
      )
    }

  def getString(
                 key: String
               ): Either[String, Option[Array[Byte]]] =
    lock.synchronized {
      removeIfExpired(key)

      data.get(key) match
        case None =>
          Right(None)

        case Some(RedisEntry(RedisString(bytes), _)) =>
          Right(Some(bytes.clone()))

        case Some(_) =>
          Left("WRONGTYPE Operation against a key holding the wrong kind of value")
    }

  def delete(key: String): Boolean =
    lock.synchronized {
      removeIfExpired(key)
      data.remove(key).isDefined
    }

  def deleteMany(keys: List[String]): Long =
    lock.synchronized {
      keys.count { key =>
        removeIfExpired(key)
        data.remove(key).isDefined
      }.toLong
    }

  def existsMany(keys: List[String]): Long =
    lock.synchronized {
      keys.count { key =>
        removeIfExpired(key)
        data.contains(key)
      }.toLong
    }

  def expireAt(
                key: String,
                timestampMillis: Long
              ): Boolean =
    lock.synchronized {
      removeIfExpired(key)

      data.get(key) match
        case None =>
          false

        case Some(entry) =>
          if timestampMillis <= currentTimeMillis() then
            data.remove(key)
            true
          else
            data.update(
              key,
              entry.copy(expiresAtMillis = Some(timestampMillis))
            )
            true
    }

  def ttlSeconds(key: String): Long =
    lock.synchronized {
      removeIfExpired(key)

      data.get(key) match
        case None =>
          -2L

        case Some(RedisEntry(_, None)) =>
          -1L

        case Some(RedisEntry(_, Some(expiresAtMillis))) =>
          val remainingMillis =
            expiresAtMillis - currentTimeMillis()

          if remainingMillis <= 0 then
            data.remove(key)
            -2L
          else
            // Round up so 1500ms becomes 2 seconds.
            (remainingMillis + 999L) / 1000L
    }

  def clear(): Unit =
    lock.synchronized {
      data.clear()
    }

  private def removeIfExpired(key: String): Unit =
    data.get(key) match
      case Some(entry) if isExpired(entry) =>
        data.remove(key)

      case _ =>
        ()

  private def isExpired(entry: RedisEntry): Boolean =
    entry.expiresAtMillis.exists(_ <= currentTimeMillis())

  private def currentTimeMillis(): Long =
    System.currentTimeMillis()