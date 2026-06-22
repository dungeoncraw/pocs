package parser

import java.nio.charset.StandardCharsets

object RespValues:

  def simple(value: String): RespValue =
    RespSimpleString(value)

  def error(value: String): RespValue =
    RespError(value)

  def integer(value: Long): RespValue =
    RespInteger(value)

  def bulkString(value: String): RespValue =
    RespBulkString(Some(value.getBytes(StandardCharsets.UTF_8)))

  def bulkBytes(value: Array[Byte]): RespValue =
    RespBulkString(Some(value))

  def nullBulkString: RespValue =
    RespBulkString(None)

  def array(values: List[RespValue]): RespValue =
    RespArray(Some(values))

  def nullArray: RespValue =
    RespArray(None)