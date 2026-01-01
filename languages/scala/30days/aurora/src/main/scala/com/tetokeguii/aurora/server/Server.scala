package com.tetokeguii.aurora.server

import cask.*
import com.tetokeguii.aurora.core.*

class Server(publicDir: os.Path, val overridePort: Int) extends Routes:
  @get("/")
  def index() = StaticFile( (publicDir / ProjectPaths.IndexHtml.path).toString, headers = Seq("Content-Type" -> "text/html"))

  @get("/posts/:name")
  def post(name: String) = StaticFile( (publicDir / ProjectPaths.Posts.path / name).toString, headers = Seq("Content-Type" -> "text/html"))

  @get("/assets", subpath = true)
  def assets(request: Request) = 
    val path = request.remainingPathSegments.mkString("/")
    StaticFile( (publicDir / ProjectPaths.Assets.path / path).toString, headers = Nil )

  @get("/search.html")
  def search() = Templates.search("Search") // This is a bit hacky, title should come from config

  @get("/tags/:name")
  def tag(name: String) = StaticFile( (publicDir / ProjectPaths.Tags.path / name).toString, headers = Seq("Content-Type" -> "text/html"))

  @get("/search-index.json")
  def searchIndex() = StaticFile( (publicDir / ProjectPaths.SearchIndexJson.path).toString, headers = Seq("Content-Type" -> "application/json"))

  @get("/sitemap.xml")
  def sitemap() = StaticFile( (publicDir / ProjectPaths.SitemapXml.path).toString, headers = Seq("Content-Type" -> "application/xml"))

  @get("/build-report.json")
  def buildReport() = StaticFile( (publicDir / ProjectPaths.BuildReportJson.path).toString, headers = Seq("Content-Type" -> "application/json"))

  @get("/__health")
  def health() = "OK"

  @get("/__stats")
  def stats() = 
    val stats = Map(
      "uptime_ms" -> (System.currentTimeMillis() - startTime).toString,
      "posts_count" -> os.list(publicDir / ProjectPaths.Posts.path).size.toString,
      "tags_count" -> (if (os.exists(publicDir / ProjectPaths.Tags.path)) os.list(publicDir / ProjectPaths.Tags.path).size.toString else "0"),
      "public_dir_size_bytes" -> os.walk(publicDir).filter(os.isFile).map(os.size).sum.toString
    )
    upickle.default.write(stats)

  private val clients = java.util.concurrent.ConcurrentHashMap.newKeySet[java.io.PrintWriter]()

  @get("/sse")
  def sse() =
    cask.Response(
      data = "data: connected\n\n",
      headers = Seq(
        "Content-Type" -> "text/event-stream",
        "Cache-Control" -> "no-cache",
        "Connection" -> "keep-alive"
      )
    )

  def notifyReload(): Unit =
    clients.forEach { writer =>
      writer.println("data: reload\n")
    }

  private val startTime = System.currentTimeMillis()

  initialize()

object ServerApp extends Main:
  val publicDir = os.pwd / ProjectPaths.Public.path
  val server = new Server(publicDir, 8080)
  override def allRoutes = Seq(server)
  override def port: Int = 8080
  override def host: String = "0.0.0.0"
