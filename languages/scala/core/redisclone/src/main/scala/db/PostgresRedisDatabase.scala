package db

import java.sql.{Connection, DriverManager}

final class PostgresRedisDatabase extends RedisDatabase:

  private val config = RedisDatabase.config
  private val url = config.getProperty("db.postgres.url", "jdbc:postgresql://localhost:5432/redisclone")
  private val user = config.getProperty("db.postgres.user", "postgres")
  private val password = config.getProperty("db.postgres.password", "postgres")

  initTable()

  private def getConnection: Connection =
    DriverManager.getConnection(url, user, password)

  private def initTable(): Unit =
    val conn = getConnection
    try
      val stmt = conn.createStatement()
      stmt.execute(
        """
          |CREATE TABLE IF NOT EXISTS redis_data (
          |  key TEXT PRIMARY KEY,
          |  value BYTEA NOT NULL,
          |  expires_at_millis BIGINT
          |);
          |""".stripMargin
      )
      stmt.close()
    finally
      conn.close()

  def setString(key: String, value: Array[Byte]): Unit =
    val conn = getConnection
    try
      val stmt = conn.prepareStatement(
        "INSERT INTO redis_data (key, value, expires_at_millis) VALUES (?, ?, NULL) ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value, expires_at_millis = NULL"
      )
      stmt.setString(1, key)
      stmt.setBytes(2, value)
      stmt.executeUpdate()
      stmt.close()
    finally
      conn.close()

  def getString(key: String): Either[String, Option[Array[Byte]]] =
    val conn = getConnection
    try
      val now = System.currentTimeMillis()
      val stmt = conn.prepareStatement(
        "SELECT value, expires_at_millis FROM redis_data WHERE key = ?"
      )
      stmt.setString(1, key)
      val rs = stmt.executeQuery()
      if rs.next() then
        val expiresAt = rs.getLong("expires_at_millis")
        if !rs.wasNull() && expiresAt <= now then
          delete(key)
          Right(None)
        else
          Right(Some(rs.getBytes("value")))
      else
        Right(None)
    finally
      conn.close()

  def delete(key: String): Boolean =
    val conn = getConnection
    try
      val stmt = conn.prepareStatement("DELETE FROM redis_data WHERE key = ?")
      stmt.setString(1, key)
      val affected = stmt.executeUpdate()
      affected > 0
    finally
      conn.close()

  def deleteMany(keys: List[String]): Long =
    if keys.isEmpty then return 0L
    val conn = getConnection
    try
      val placeholders = keys.map(_ => "?").mkString(",")
      val stmt = conn.prepareStatement(s"DELETE FROM redis_data WHERE key IN ($placeholders)")
      keys.zipWithIndex.foreach { case (key, i) => stmt.setString(i + 1, key) }
      stmt.executeUpdate().toLong
    finally
      conn.close()

  def existsMany(keys: List[String]): Long =
    if keys.isEmpty then return 0L
    val conn = getConnection
    try
      val now = System.currentTimeMillis()
      val placeholders = keys.map(_ => "?").mkString(",")
      val stmt = conn.prepareStatement(
        s"SELECT COUNT(*) FROM redis_data WHERE key IN ($placeholders) AND (expires_at_millis IS NULL OR expires_at_millis > ?)"
      )
      keys.zipWithIndex.foreach { case (key, i) => stmt.setString(i + 1, key) }
      stmt.setLong(keys.size + 1, now)
      val rs = stmt.executeQuery()
      if rs.next() then rs.getLong(1) else 0L
    finally
      conn.close()

  def expireAt(key: String, timestampMillis: Long): Boolean =
    val conn = getConnection
    try
      val now = System.currentTimeMillis()
      if timestampMillis <= now then
        delete(key)
      else
        val stmt = conn.prepareStatement(
          "UPDATE redis_data SET expires_at_millis = ? WHERE key = ?"
        )
        stmt.setLong(1, timestampMillis)
        stmt.setString(2, key)
        val affected = stmt.executeUpdate()
        affected > 0
    finally
      conn.close()

  def ttlSeconds(key: String): Long =
    val conn = getConnection
    try
      val now = System.currentTimeMillis()
      val stmt = conn.prepareStatement(
        "SELECT expires_at_millis FROM redis_data WHERE key = ?"
      )
      stmt.setString(1, key)
      val rs = stmt.executeQuery()
      if rs.next() then
        val expiresAt = rs.getLong("expires_at_millis")
        if rs.wasNull() then
          -1L
        else
          val remainingMillis = expiresAt - now
          if remainingMillis <= 0 then
            delete(key)
            -2L
          else
            (remainingMillis + 999L) / 1000L
      else
        -2L
    finally
      conn.close()

  def clear(): Unit =
    val conn = getConnection
    try
      val stmt = conn.createStatement()
      stmt.execute("TRUNCATE TABLE redis_data")
      stmt.close()
    finally
      conn.close()
