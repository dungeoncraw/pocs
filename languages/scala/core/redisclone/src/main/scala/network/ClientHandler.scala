package network

import parser.{Command, RespParseException, RespParser}

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
        val respValue = parser.parseValue()
        val commandResult = Command.fromResp(respValue)

        val response =
          commandResult match
            case Right(command) =>
              handleCommand(command)

            case Left(error) =>
              s"-$error\r\n"

        output.write(response.getBytes)
        output.flush()

    catch
      case error: RespParseException =>
        println(s"RESP parse error: ${error.getMessage}")

      case NonFatal(error) =>
        println(s"Client error: ${error.getMessage}")

    finally
      println(s"Client disconnected: ${socket.getRemoteSocketAddress}")
      socket.close()

  private def handleCommand(command: Command): String =
    command.name match
      case "PING" =>
        "+PONG\r\n"

      case "SET" =>
        "+OK\r\n"

      case "GET" =>
        "$-1\r\n"

      case other =>
        s"-ERR unknown command '$other'\r\n"


object ClientHandler:

  def apply(socket: Socket): ClientHandler =
    new ClientHandler(socket)