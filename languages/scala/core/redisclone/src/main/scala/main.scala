import server.TcpServer

@main
def main(): Unit = {
  val server = TcpServer(
    host = "0.0.0.0",
    port = 6378
  )

  server.start()
}

