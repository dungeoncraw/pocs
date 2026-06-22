package server

import db.RedisDatabase
import server.*
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream, OutputStream}
import java.net.{InetAddress, Socket, SocketAddress}
import java.nio.charset.StandardCharsets

class ClientHandlerTest extends munit.FunSuite {
  
  override def beforeEach(context: BeforeEach): Unit = {
    RedisDatabase.instance.clear()
  }

  test("handle PING command") {
    val input = "*1\r\n$4\r\nPING\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), "+PONG\r\n")
  }

  test("handle SET command") {
    val input = "*3\r\n$3\r\nSET\r\n$3\r\nkey\r\n$5\r\nvalue\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), "+OK\r\n")
  }

  test("handle GET command") {
    val input = "*2\r\n$3\r\nGET\r\n$3\r\nkey\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), "$-1\r\n")
  }

  test("handle unknown command") {
    val input = "*1\r\n$4\r\nUNKN\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), "-ERR unknown command 'UNKN'\r\n")
  }

  test("handle DEL command") {
    // First set a key
    RedisDatabase.instance.setString("key1", "val1".getBytes)
    
    val input = "*2\r\n$3\r\nDEL\r\n$4\r\nkey1\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), ":1\r\n")
  }

  test("handle EXISTS command") {
    // First set a key
    RedisDatabase.instance.setString("key1", "val1".getBytes)
    
    val input = "*3\r\n$6\r\nEXISTS\r\n$4\r\nkey1\r\n$4\r\nkey2\r\n"
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()

    val mockSocket = new MockSocket(in, out)
    val handler = new ClientHandler(mockSocket)

    handler.handle()

    assertEquals(new String(out.toByteArray, StandardCharsets.UTF_8), ":1\r\n")
  }

  class MockSocket(in: InputStream, out: OutputStream) extends Socket {
    override def getInputStream: InputStream = in
    override def getOutputStream: OutputStream = out
    override def getRemoteSocketAddress: SocketAddress = new SocketAddress {}
    override def close(): Unit = ()
  }
}
