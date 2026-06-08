package parser

import java.nio.charset.StandardCharsets

@main def testRespEncoder(): Unit =

  def printEncoded(value: RespValue): Unit =
    val bytes = RespEncoder.encode(value)
    val text = String(bytes, StandardCharsets.UTF_8)

    println("Value:")
    println(value)

    println("Encoded:")
    println(text.replace("\r", "\\r").replace("\n", "\\n\n"))

    println()

  printEncoded(RespSimpleString("OK"))

  printEncoded(RespError("ERR unknown command"))

  printEncoded(RespInteger(123))

  printEncoded(
    RespBulkString(
      Some("Thiago".getBytes(StandardCharsets.UTF_8))
    )
  )

  printEncoded(RespBulkString(None))

  printEncoded(
    RespArray(
      Some(
        List(
          RespBulkString(Some("GET".getBytes(StandardCharsets.UTF_8))),
          RespBulkString(Some("name".getBytes(StandardCharsets.UTF_8)))
        )
      )
    )
  )