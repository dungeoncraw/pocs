package network

import java.net.{InetSocketAddress, ServerSocket}

final class TcpServer(
                       host: String,
                       port: Int
                     ):

  private val serverSocket = new ServerSocket()

  def start(): Unit =
    serverSocket.bind(new InetSocketAddress(host, port))

    println(s"Redis clone TCP server running on $host:$port")

    while true do
      val clientSocket = serverSocket.accept()

      println(
        s"Client connected: ${clientSocket.getRemoteSocketAddress}"
      )

      val handler = ClientHandler(clientSocket)

      val thread = Thread(() => handler.handle())
      thread.start()

object TcpServer:

  def apply(host: String, port: Int): TcpServer =
    new TcpServer(host, port)