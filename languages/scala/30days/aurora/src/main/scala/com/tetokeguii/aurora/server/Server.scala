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
      "uptime" -> "unknown",
      "posts_count" -> os.list(publicDir / ProjectPaths.Posts.path).size.toString
    )
    upickle.default.write(stats)

  initialize()

object ServerApp extends Main:
  val publicDir = os.pwd / ProjectPaths.Public.path
  val server = new Server(publicDir, 8080)
  override def allRoutes = Seq(server)
  override def port: Int = 8080
  override def host: String = "0.0.0.0"
