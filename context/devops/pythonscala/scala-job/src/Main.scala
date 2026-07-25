//> using scala "3.6.3"
//> using jvm "21"

package localjob

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant
import scala.util.control.NonFatal

object Main:
  def main(args: Array[String]): Unit =
    val baseUrl =
      sys.env.getOrElse("DJANGO_BASE_URL", "http://127.0.0.1:8000").stripSuffix("/")

    val endpoint = s"$baseUrl/api/ping/"
    val podName = sys.env.getOrElse("POD_NAME", "unknown")
    val startedAt = Instant.now()

    println(s"[scala-job] Started at: $startedAt")
    println(s"[scala-job] Pod: $podName")
    println(s"[scala-job] GET $endpoint")

    try
      val client =
        HttpClient
          .newBuilder()
          .connectTimeout(Duration.ofSeconds(5))
          .build()

      val request =
        HttpRequest
          .newBuilder()
          .uri(URI.create(endpoint))
          .timeout(Duration.ofSeconds(15))
          .header("Accept", "application/json")
          .header("User-Agent", "scala3-cron-job/1.0")
          .GET()
          .build()

      val response =
        client.send(request, HttpResponse.BodyHandlers.ofString())

      println(s"[scala-job] HTTP ${response.statusCode()}")
      println(s"[scala-job] Response: ${response.body()}")

      if response.statusCode() < 200 || response.statusCode() >= 300 then
        System.err.println("[scala-job] The HTTP response indicates a failure.")
        sys.exit(1)

      println(s"[scala-job] Completed successfully at ${Instant.now()}")

    catch
      case NonFatal(error) =>
        System.err.println(
          s"[scala-job] Error while running the job: ${error.getClass.getSimpleName}: ${error.getMessage}"
        )
        error.printStackTrace(System.err)
        sys.exit(1)
