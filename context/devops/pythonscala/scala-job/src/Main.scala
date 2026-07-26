//> using scala "3.6.3"
//> using jvm "system"

package localjob

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

import java.io.{BufferedWriter, IOException}
import java.net.InetSocketAddress
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.{
  AtomicMoveNotSupportedException,
  Files,
  Path,
  Paths,
  StandardCopyOption,
  StandardOpenOption
}
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.{CountDownLatch, Executors}
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

final case class CsvRecord(
    id: String,
    name: String,
    value: String,
    updatedAt: String,
    updatedBy: String
)

final case class UpsertResult(record: CsvRecord, created: Boolean, total: Int)

object JsonCodec {
  private val StringField =
    """"((?:\\.|[^"\\])*)"\s*:\s*"((?:\\.|[^"\\])*)"""".r

  def parseStringObject(input: String): Either[String, Map[String, String]] = {
    val fields =
      StringField
        .findAllMatchIn(input)
        .map { matched =>
          unescape(matched.group(1)) -> unescape(matched.group(2))
        }
        .toMap

    if (fields.isEmpty) Left("The request body must contain a JSON object with string fields")
    else Right(fields)
  }

  def escape(value: String): String = {
    val output = new StringBuilder

    value.foreach {
      case '"'  => output.append("\\\"")
      case '\\' => output.append("\\\\")
      case '\b' => output.append("\\b")
      case '\f' => output.append("\\f")
      case '\n' => output.append("\\n")
      case '\r' => output.append("\\r")
      case '\t' => output.append("\\t")
      case character if character < ' ' =>
        output.append(f"\\u${character.toInt}%04x")
      case character =>
        output.append(character)
    }

    output.result()
  }

  private def unescape(value: String): String = {
    val output = new StringBuilder
    var index = 0

    while (index < value.length) {
      val character = value.charAt(index)

      if (character != '\\') {
        output.append(character)
      } else {
        if (index + 1 >= value.length) {
          throw new IllegalArgumentException("Invalid JSON escape sequence")
        }

        index += 1
        value.charAt(index) match {
          case '"'  => output.append('"')
          case '\\' => output.append('\\')
          case '/'  => output.append('/')
          case 'b'  => output.append('\b')
          case 'f'  => output.append('\f')
          case 'n'  => output.append('\n')
          case 'r'  => output.append('\r')
          case 't'  => output.append('\t')
          case 'u' =>
            if (index + 4 >= value.length) {
              throw new IllegalArgumentException("Invalid JSON unicode escape")
            }
            val hexadecimal = value.substring(index + 1, index + 5)
            output.append(Integer.parseInt(hexadecimal, 16).toChar)
            index += 4
          case other =>
            throw new IllegalArgumentException(s"Unsupported JSON escape: \\$other")
        }
      }

      index += 1
    }

    output.result()
  }

  def record(record: CsvRecord): String =
    s"""{"id":"${escape(record.id)}","name":"${escape(record.name)}","value":"${escape(record.value)}","updated_at":"${escape(record.updatedAt)}","updated_by":"${escape(record.updatedBy)}"}"""

  def records(values: Vector[CsvRecord]): String =
    values.map(record).mkString("[", ",", "]")

  def error(message: String): String =
    s"""{"implementation":"scala","error":"${escape(message)}"}"""
}

object CsvStore {
  private val Headers = Vector("id", "name", "value", "updated_at", "updated_by")

  private val CsvPath =
    Paths.get(sys.env.getOrElse("CSV_PATH", "/data/records.csv"))

  private val LockPath =
    Paths.get(sys.env.getOrElse("CSV_LOCK_PATH", "/data/records.lock"))

  def ensureStorageReady(): Unit = {
    Option(CsvPath.getParent).foreach(path => Files.createDirectories(path))
    Option(LockPath.getParent).foreach(path => Files.createDirectories(path))

    val directory = Option(CsvPath.getParent).getOrElse(Paths.get("."))
    if (!Files.isWritable(directory)) {
      throw new IOException(s"CSV directory is not writable: $directory")
    }
  }

  def validatePayload(fields: Map[String, String]): (String, String, String) = {
    val id = validateText("id", fields.getOrElse("id", ""), required = true, 100)
    val name = validateText("name", fields.getOrElse("name", ""), required = true, 200)
    val value = validateText("value", fields.getOrElse("value", ""), required = false, 1000)
    (id, name, value)
  }

  private def validateText(
      field: String,
      rawValue: String,
      required: Boolean,
      maxLength: Int
  ): String = {
    val value = if (field == "id" || field == "name") rawValue.trim else rawValue

    if (required && value.isEmpty) {
      throw new IllegalArgumentException(s"$field must not be empty")
    }

    if (value.length > maxLength) {
      throw new IllegalArgumentException(
        s"$field must contain at most $maxLength characters"
      )
    }

    if (value.exists(character =>
          character == '\r' || character == '\n' || character == '\u0000'
        )) {
      throw new IllegalArgumentException(
        s"$field must not contain line breaks or NUL"
      )
    }

    value
  }

  def listRecords(): Vector[CsvRecord] =
    withLock(shared = true) {
      readRecordsUnlocked()
    }

  def upsert(
      id: String,
      name: String,
      value: String,
      updatedBy: String
  ): UpsertResult =
    withLock(shared = false) {
      val current = readRecordsUnlocked()
      val record =
        CsvRecord(
          id = id,
          name = name,
          value = value,
          updatedAt = Instant.now().toString,
          updatedBy = updatedBy
        )

      val existingIndex = current.indexWhere(_.id == id)
      val created = existingIndex < 0

      val updated =
        if (created) current :+ record
        else current.updated(existingIndex, record)

      writeRecordsUnlocked(updated)
      UpsertResult(record, created, updated.size)
    }

  private def withLock[T](shared: Boolean)(operation: => T): T = {
    ensureStorageReady()

    val channel =
      FileChannel.open(
        LockPath,
        StandardOpenOption.CREATE,
        StandardOpenOption.READ,
        StandardOpenOption.WRITE
      )

    val lock = channel.lock(0L, Long.MaxValue, shared)

    try operation
    finally {
      lock.release()
      channel.close()
    }
  }

  private def readRecordsUnlocked(): Vector[CsvRecord] = {
    if (!Files.exists(CsvPath) || Files.size(CsvPath) == 0L) {
      Vector.empty
    } else {
      val lines = Files.readAllLines(CsvPath, StandardCharsets.UTF_8).asScala.toVector

      if (lines.isEmpty) {
        Vector.empty
      } else {
        val header = parseCsvLine(lines.head)

        if (header != Headers) {
          throw new IOException(
            s"Unexpected CSV header. Expected ${Headers.mkString(",")}, got ${header.mkString(",")}"
          )
        }

        lines.tail
          .filter(_.nonEmpty)
          .map { line =>
            val fields = parseCsvLine(line)

            if (fields.size != Headers.size) {
              throw new IOException(s"Invalid CSV row with ${fields.size} fields")
            }

            CsvRecord(
              id = fields(0),
              name = fields(1),
              value = fields(2),
              updatedAt = fields(3),
              updatedBy = fields(4)
            )
          }
      }
    }
  }

  private def writeRecordsUnlocked(records: Vector[CsvRecord]): Unit = {
    ensureStorageReady()

    val parent = Option(CsvPath.getParent).getOrElse(Paths.get("."))
    val temporary = Files.createTempFile(parent, "records-", ".tmp")

    try {
      val writer =
        Files.newBufferedWriter(
          temporary,
          StandardCharsets.UTF_8,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE
        )

      try {
        writer.write(Headers.map(escapeCsv).mkString(","))
        writer.newLine()

        records.foreach { record =>
          writer.write(
            Vector(
              record.id,
              record.name,
              record.value,
              record.updatedAt,
              record.updatedBy
            ).map(escapeCsv).mkString(",")
          )
          writer.newLine()
        }

        writer.flush()
      } finally {
        writer.close()
      }

      val forceChannel =
        FileChannel.open(temporary, StandardOpenOption.WRITE)

      try forceChannel.force(true)
      finally forceChannel.close()

      try {
        Files.move(
          temporary,
          CsvPath,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING
        )
      } catch {
        case _: AtomicMoveNotSupportedException =>
          Files.move(
            temporary,
            CsvPath,
            StandardCopyOption.REPLACE_EXISTING
          )
      }
    } finally {
      Files.deleteIfExists(temporary)
    }
  }

  private def escapeCsv(value: String): String = {
    val escaped = value.replace("\"", "\"\"")
    if (value.exists(character =>
          character == ',' || character == '"' || character == '\r' || character == '\n'
        )) s""""$escaped""""
    else escaped
  }

  private def parseCsvLine(line: String): Vector[String] = {
    val fields = ArrayBuffer.empty[String]
    val current = new StringBuilder
    var insideQuotes = false
    var index = 0

    while (index < line.length) {
      val character = line.charAt(index)

      if (character == '"') {
        if (insideQuotes && index + 1 < line.length && line.charAt(index + 1) == '"') {
          current.append('"')
          index += 1
        } else {
          insideQuotes = !insideQuotes
        }
      } else if (character == ',' && !insideQuotes) {
        fields += current.result()
        current.clear()
      } else {
        current.append(character)
      }

      index += 1
    }

    if (insideQuotes) {
      throw new IOException("Unclosed quoted CSV field")
    }

    fields += current.result()
    fields.toVector
  }
}

object ApiServer {
  private val Port = sys.env.get("SCALA_PORT").flatMap(_.toIntOption).getOrElse(9000)
  private val Host = sys.env.getOrElse("SCALA_HOST", "0.0.0.0")

  def run(): Unit = {
    CsvStore.ensureStorageReady()

    val server = HttpServer.create(new InetSocketAddress(Host, Port), 0)
    server.createContext("/", new RequestHandler)
    server.setExecutor(Executors.newFixedThreadPool(8))
    server.start()

    println(s"[scala-api] Listening on http://$Host:$Port")
    println(s"[scala-api] CSV path: ${sys.env.getOrElse("CSV_PATH", "/data/records.csv")}")

    new CountDownLatch(1).await()
  }

  private final class RequestHandler extends HttpHandler {
    override def handle(exchange: HttpExchange): Unit = {
      try {
        val path = normalizePath(exchange.getRequestURI.getPath)
        val method = exchange.getRequestMethod

        (method, path) match {
          case ("GET", "/") =>
            sendJson(
              exchange,
              200,
              """{"application":"scala-csv-api","implementation":"scala","endpoints":{"upsert":"POST /api/csv/upsert","list":"GET /api/csv/records","live":"GET /health/live","ready":"GET /health/ready"}}"""
            )

          case ("GET", "/health/live") =>
            sendJson(exchange, 200, """{"status":"alive","implementation":"scala"}""")

          case ("GET", "/health/ready") =>
            try {
              CsvStore.ensureStorageReady()
              sendJson(exchange, 200, """{"status":"ready","implementation":"scala"}""")
            } catch {
              case NonFatal(error) =>
                sendJson(exchange, 503, JsonCodec.error(error.getMessage))
            }

          case ("GET", "/api/csv/records") =>
            val records = CsvStore.listRecords()
            sendJson(
              exchange,
              200,
              s"""{"implementation":"scala","count":${records.size},"records":${JsonCodec.records(records)}}"""
            )

          case ("POST", "/api/csv/upsert") =>
            val body = readBody(exchange)
            JsonCodec.parseStringObject(body) match {
              case Left(error) =>
                sendJson(exchange, 400, JsonCodec.error(error))

              case Right(fields) =>
                try {
                  val (id, name, value) = CsvStore.validatePayload(fields)
                  val result =
                    CsvStore.upsert(
                      id = id,
                      name = name,
                      value = value,
                      updatedBy = "scala-api"
                    )

                  val status = if (result.created) 201 else 200
                  sendJson(
                    exchange,
                    status,
                    s"""{"implementation":"scala","created":${result.created},"total":${result.total},"record":${JsonCodec.record(result.record)}}"""
                  )
                } catch {
                  case error: IllegalArgumentException =>
                    sendJson(exchange, 400, JsonCodec.error(error.getMessage))
                }
            }

          case (_, "/api/csv/upsert") =>
            exchange.getResponseHeaders.set("Allow", "POST")
            sendJson(exchange, 405, JsonCodec.error("Method not allowed. Use: POST"))

          case (_, "/api/csv/records") =>
            exchange.getResponseHeaders.set("Allow", "GET")
            sendJson(exchange, 405, JsonCodec.error("Method not allowed. Use: GET"))

          case _ =>
            sendJson(exchange, 404, JsonCodec.error("Endpoint not found"))
        }
      } catch {
        case NonFatal(error) =>
          error.printStackTrace(System.err)
          sendJson(exchange, 500, JsonCodec.error(error.getMessage))
      } finally {
        exchange.close()
      }
    }
  }

  private def normalizePath(path: String): String =
    if (path.length > 1 && path.endsWith("/")) path.dropRight(1) else path

  private def readBody(exchange: HttpExchange): String = {
    val bytes = exchange.getRequestBody.readAllBytes()

    if (bytes.length > 16 * 1024) {
      throw new IllegalArgumentException("Request body is too large")
    }

    new String(bytes, StandardCharsets.UTF_8)
  }

  private def sendJson(exchange: HttpExchange, status: Int, body: String): Unit = {
    val bytes = body.getBytes(StandardCharsets.UTF_8)
    exchange.getResponseHeaders.set("Content-Type", "application/json; charset=utf-8")
    exchange.getResponseHeaders.set("Cache-Control", "no-store")
    exchange.sendResponseHeaders(status, bytes.length.toLong)
    val output = exchange.getResponseBody
    try output.write(bytes)
    finally output.close()
  }
}

object ScheduledJob {
  def run(): Unit = {
    val now = Instant.now()
    val bucket = now.truncatedTo(ChronoUnit.MINUTES)
    val id = s"cron-$bucket"

    val result =
      CsvStore.upsert(
        id = id,
        name = "scala-scheduled-job",
        value = s"Scheduled CSV update at $now",
        updatedBy = "scala-cron"
      )

    println(
      s"""[scala-cron] ${if (result.created) "Created" else "Updated"} ${JsonCodec.record(result.record)}"""
    )
    println(s"[scala-cron] Total CSV records: ${result.total}")
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    args.headOption.getOrElse("cron") match {
      case "server" => ApiServer.run()
      case "cron"   => ScheduledJob.run()
      case other =>
        System.err.println(s"Unknown mode: $other. Use 'server' or 'cron'.")
        sys.exit(2)
    }
  }
}
