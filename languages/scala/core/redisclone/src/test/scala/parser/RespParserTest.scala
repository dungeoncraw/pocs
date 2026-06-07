package parser

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class RespParserTest extends munit.FunSuite {

  test("parse simple string") {
    val input = "+OK\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    assertEquals(result, RespSimpleString("OK"))
  }

  test("parse error") {
    val input = "-Error message\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    assertEquals(result, RespError("Error message"))
  }

  test("parse integer") {
    val input = ":1000\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    assertEquals(result, RespInteger(1000L))
  }

  test("parse bulk string") {
    val input = "$5\r\nhello\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    result match {
      case RespBulkString(Some(bytes)) =>
        assertEquals(new String(bytes, StandardCharsets.UTF_8), "hello")
      case _ => fail("Expected RespBulkString with content")
    }
  }

  test("parse null bulk string") {
    val input = "$-1\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    assertEquals(result, RespBulkString(None))
  }

  test("parse array") {
    val input = "*2\r\n$5\r\nhello\r\n$5\r\nworld\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    result match {
      case RespArray(Some(List(RespBulkString(Some(b1)), RespBulkString(Some(b2))))) =>
        assertEquals(new String(b1, StandardCharsets.UTF_8), "hello")
        assertEquals(new String(b2, StandardCharsets.UTF_8), "world")
      case _ => fail(s"Expected RespArray with 2 bulk strings, got $result")
    }
  }

  test("parse null array") {
    val input = "*-1\r\n"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    val result = parser.parseValue()
    assertEquals(result, RespArray(None))
  }

  test("throw exception on invalid prefix") {
    val input = "invalid"
    val parser = RespParser(new ByteArrayInputStream(input.getBytes))
    intercept[RespParseException] {
      parser.parseValue()
    }
  }
}
