package db

sealed trait RedisValue

final case class RedisString(value: Array[Byte]) extends RedisValue

final case class RedisEntry(
                             value: RedisValue,
                             expiresAtMillis: Option[Long]
                           )