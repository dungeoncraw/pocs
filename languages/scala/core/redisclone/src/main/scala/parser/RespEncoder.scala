package parser

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

object RespEncoder:

  private val CRLF: Array[Byte] =
    "\r\n".getBytes(StandardCharsets.UTF_8)

  def encode(value: RespValue): Array[Byte] =
    val output = ByteArrayOutputStream()
    writeValue(output, value)
    output.toByteArray

  private def writeValue(
                          output: ByteArrayOutputStream,
                          value: RespValue
                        ): Unit =
    value match
      case RespSimpleString(text) =>
        writeAscii(output, "+")
        writeUtf8(output, text)
        writeCRLF(output)

      case RespError(message) =>
        writeAscii(output, "-")
        writeUtf8(output, message)
        writeCRLF(output)

      case RespInteger(number) =>
        writeAscii(output, ":")
        writeAscii(output, number.toString)
        writeCRLF(output)

      case RespBulkString(Some(bytes)) =>
        writeAscii(output, "$")
        writeAscii(output, bytes.length.toString)
        writeCRLF(output)
        output.write(bytes)
        writeCRLF(output)

      case RespBulkString(None) =>
        writeAscii(output, "$-1")
        writeCRLF(output)

      case RespArray(Some(values)) =>
        writeAscii(output, "*")
        writeAscii(output, values.length.toString)
        writeCRLF(output)

        values.foreach { item =>
          writeValue(output, item)
        }

      case RespArray(None) =>
        writeAscii(output, "*-1")
        writeCRLF(output)

  private def writeCRLF(output: ByteArrayOutputStream): Unit =
    output.write(CRLF)

  private def writeAscii(
                          output: ByteArrayOutputStream,
                          text: String
                        ): Unit =
    output.write(text.getBytes(StandardCharsets.US_ASCII))

  private def writeUtf8(
                         output: ByteArrayOutputStream,
                         text: String
                       ): Unit =
    output.write(text.getBytes(StandardCharsets.UTF_8))