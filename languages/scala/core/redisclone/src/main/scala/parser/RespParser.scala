package parser

import java.io.InputStream
import java.nio.charset.StandardCharsets
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

final class RespParseException(message: String)
  extends RuntimeException(message)

final class RespParser(input: InputStream):

  private val CR = '\r'.toInt
  private val LF = '\n'.toInt

  def parseValue(): RespValue =
    val prefix = readByteOrFail("Connection closed while reading RESP prefix")

    prefix.toChar match
      case '+' =>
        parseSimpleString()

      case '-' =>
        parseError()

      case ':' =>
        parseInteger()

      case '$' =>
        parseBulkString()

      case '*' =>
        parseArray()

      case other =>
        throw RespParseException(s"Invalid RESP prefix: '$other'")

  private def parseSimpleString(): RespValue =
    val line = readLineAsString()
    RespSimpleString(line)

  private def parseError(): RespValue =
    val line = readLineAsString()
    RespError(line)

  private def parseInteger(): RespValue =
    val line = readLineAsString()

    val value =
      Try(line.toLong).getOrElse {
        throw RespParseException(s"Invalid RESP integer: $line")
      }

    RespInteger(value)

  private def parseBulkString(): RespValue =
    val line = readLineAsString()

    val length =
      Try(line.toInt).getOrElse {
        throw RespParseException(s"Invalid bulk string length: $line")
      }

    if length == -1 then
      RespBulkString(None)
    else if length < -1 then
      throw RespParseException(s"Invalid bulk string length: $length")
    else
      val content = readExactly(length)
      expectCRLF()
      RespBulkString(Some(content))

  private def parseArray(): RespValue =
    val line = readLineAsString()

    val count =
      Try(line.toInt).getOrElse {
        throw RespParseException(s"Invalid array length: $line")
      }

    if count == -1 then
      RespArray(None)
    else if count < -1 then
      throw RespParseException(s"Invalid array length: $count")
    else
      val values = List.newBuilder[RespValue]

      var index = 0
      while index < count do
        values += parseValue()
        index = index + 1

      RespArray(Some(values.result()))

  private def readLineAsString(): String =
    val bytes = readLineBytes()
    String(bytes, StandardCharsets.UTF_8)

  private def readLineBytes(): Array[Byte] =
    val buffer = ArrayBuffer.empty[Byte]

    var previous = -1
    var current = readByteOrFail("Unexpected end of stream while reading line")

    while true do
      if previous == CR && current == LF then
        // Remove the CR that was added during the previous loop.
        buffer.remove(buffer.length - 1)
        return buffer.toArray

      buffer.addOne(current.toByte)

      previous = current
      current = readByteOrFail("Unexpected end of stream while reading line")

    throw RespParseException("Unreachable parser state")

  private def readExactly(length: Int): Array[Byte] =
    val bytes = Array.ofDim[Byte](length)

    var offset = 0

    while offset < length do
      val bytesRead = input.read(bytes, offset, length - offset)

      if bytesRead == -1 then
        throw RespParseException(
          s"Unexpected end of stream while reading $length bytes"
        )

      offset = offset + bytesRead

    bytes

  private def expectCRLF(): Unit =
    val first = readByteOrFail("Expected CRLF, but stream ended before CR")
    val second = readByteOrFail("Expected CRLF, but stream ended before LF")

    if first != CR || second != LF then
      throw RespParseException("Expected CRLF after bulk string content")

  private def readByteOrFail(errorMessage: String): Int =
    val byte = input.read()

    if byte == -1 then
      throw RespParseException(errorMessage)

    byte