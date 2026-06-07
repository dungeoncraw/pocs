package parser

sealed trait RespValue

final case class RespSimpleString(value: String) extends RespValue

final case class RespError(value: String) extends RespValue

final case class RespInteger(value: Long) extends RespValue

final case class RespBulkString(value: Option[Array[Byte]]) extends RespValue

final case class RespArray(value: Option[List[RespValue]]) extends RespValue