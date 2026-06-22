package db

import java.io.InputStream
import java.util.Properties

trait RedisDatabase:
  def setString(key: String, value: Array[Byte]): Unit
  def getString(key: String): Either[String, Option[Array[Byte]]]
  def delete(key: String): Boolean
  def deleteMany(keys: List[String]): Long
  def existsMany(keys: List[String]): Long
  def expireAt(key: String, timestampMillis: Long): Boolean
  def ttlSeconds(key: String): Long
  def clear(): Unit

object RedisDatabase:
  private val props = new Properties()
  private val loadedProps: Properties =
    val stream: InputStream = getClass.getResourceAsStream("/application.properties")
    if (stream != null) then
      try
        props.load(stream)
        props
      finally
        stream.close()
    props

  private val dbType = loadedProps.getProperty("db.type", "in-memory")

  def config: Properties = loadedProps

  val instance: RedisDatabase = dbType match
    case "postgres" => new PostgresRedisDatabase()
    case _          => new InMemoryRedisDatabase()