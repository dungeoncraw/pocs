package parser

import java.nio.charset.StandardCharsets

class CommandTest extends munit.FunSuite {

  test("fromResp should parse a valid command") {
    val resp = RespArray(Some(List(
      RespBulkString(Some("SET".getBytes(StandardCharsets.UTF_8))),
      RespBulkString(Some("key".getBytes(StandardCharsets.UTF_8))),
      RespBulkString(Some("value".getBytes(StandardCharsets.UTF_8)))
    )))

    val result = Command.fromResp(resp)
    assertEquals(result, Right(Command("SET", List("key", "value"))))
  }

  test("fromResp should handle case-insensitivity for command name") {
    val resp = RespArray(Some(List(
      RespBulkString(Some("ping".getBytes(StandardCharsets.UTF_8)))
    )))

    val result = Command.fromResp(resp)
    assertEquals(result, Right(Command("PING", Nil)))
  }

  test("fromResp should return error for non-array") {
    val resp = RespSimpleString("PING")
    val result = Command.fromResp(resp)
    assert(result.isLeft)
    assertEquals(result.left.getOrElse(""), "ERR expected array command")
  }

  test("fromResp should return error for empty array") {
    val resp = RespArray(Some(Nil))
    val result = Command.fromResp(resp)
    assert(result.isLeft)
    assertEquals(result.left.getOrElse(""), "ERR empty command")
  }

  test("fromResp should return error for null array") {
    val resp = RespArray(None)
    val result = Command.fromResp(resp)
    assert(result.isLeft)
    assertEquals(result.left.getOrElse(""), "ERR null array is not a valid command")
  }

  test("fromResp should return error if array contains non-bulk strings") {
    val resp = RespArray(Some(List(
      RespBulkString(Some("SET".getBytes(StandardCharsets.UTF_8))),
      RespInteger(10L)
    )))

    val result = Command.fromResp(resp)
    assert(result.isLeft)
    assertEquals(result.left.getOrElse(""), "ERR command must be an array of bulk strings")
  }
}
