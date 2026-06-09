package db

import parser.{RespError, RespValue}

import java.nio.charset.StandardCharsets
import scala.util.Try

object CommandSupport:

  val WrongTypeMessage: String =
    "WRONGTYPE Operation against a key holding the wrong kind of value"

  def wrongType: RespValue =
    RespError(WrongTypeMessage)

  def wrongNumberOfArguments(commandName: String): RespValue =
    RespError(
      s"ERR wrong number of arguments for '$commandName' command"
    )

  def invalidInteger: RespValue =
    RespError("ERR value is not an integer or out of range")

  def bytes(value: String): Array[Byte] =
    value.getBytes(StandardCharsets.UTF_8)

  def parseLong(value: String): Either[RespValue, Long] =
    Try(value.toLong).toOption match
      case Some(number) =>
        Right(number)

      case None =>
        Left(invalidInteger)

  def multiplyExact(
                     left: Long,
                     right: Long
                   ): Either[RespValue, Long] =
    Try(Math.multiplyExact(left, right)).toOption match
      case Some(value) =>
        Right(value)

      case None =>
        Left(invalidInteger)

  def addExact(
                left: Long,
                right: Long
              ): Either[RespValue, Long] =
    Try(Math.addExact(left, right)).toOption match
      case Some(value) =>
        Right(value)

      case None =>
        Left(invalidInteger)