package network

import parser.{Command, RespBulkString, RespEncoder, RespError, RespParseException, RespParser, RespSimpleString, RespValue}

import java.io.{InputStream, OutputStream}
import java.net.Socket
import scala.util.control.NonFatal

final class ClientHandler(socket: Socket):

  def handle(): Unit =
    try
      val input: InputStream = socket.getInputStream
      val output: OutputStream = socket.getOutputStream

      val parser = RespParser(input)

      var running = true

      while running do
        val parsedValue = parser.parseValue()

        val responseValue =
          Command.fromResp(parsedValue) match
            case Right(command) =>
              command.name match
                case "QUIT" =>
                  running = false
                  RespSimpleString("OK")

                case other =>
                  handleCommand(command)

            case Left(error) =>
              RespError(error)

        val responseBytes =
          RespEncoder.encode(responseValue)

        output.write(responseBytes)
        output.flush()

    catch
      case error: RespParseException =>
        println(s"RESP parse error or connection closed: ${error.getMessage}")

      case NonFatal(error) =>
        println(s"Client error: ${error.getMessage}")

    finally
      println(s"Client disconnected: ${socket.getRemoteSocketAddress}")
      socket.close()

  private def handleCommand(command: Command): RespValue =
    command.name match
      case "PING" =>
        RespSimpleString("PONG")

      case "SET" =>
        RespSimpleString("OK")

      case "GET" =>
        RespBulkString(None)

      case other =>
        RespError(s"ERR unknown command '$other'")


object ClientHandler:

  def apply(socket: Socket): ClientHandler =
    new ClientHandler(socket)