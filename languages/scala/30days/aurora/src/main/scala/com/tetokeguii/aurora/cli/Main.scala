package com.tetokeguii.aurora.cli

import mainargs.*
import com.tetokeguii.aurora.core.*
import com.tetokeguii.aurora.core.Slugs.*
import com.tetokeguii.aurora.server.Server
import upickle.default.*

import java.time.LocalDate

object Commands:
  @main
  def init(): Unit =
    val config = SiteConfig(
      title = "My Aurora Site",
      description = "Built with Aurora Press",
      baseUrl = "https://example.com"
    )
    val configPath = os.pwd / ProjectPaths.Config.path
    if os.exists(configPath) then
      println("Error: config.json already exists")
    else
      os.write(configPath, write(config, indent = 2))
      os.makeDir.all(os.pwd / ProjectPaths.Content.path / ProjectPaths.Posts.path)
      os.makeDir.all(os.pwd / ProjectPaths.Templates.path)
      os.makeDir.all(os.pwd / ProjectPaths.Assets.path)
      println("Initialized Aurora project structure.")

  @main
  def newPost(@arg(doc = "Title of the post") title: String): Unit =
    val slug = Slug(title)
    val date = LocalDate.now().toString
    val frontMatter = FrontMatter(
      title = title,
      date = date,
      tags = List("draft")
    )
    val content =
      s"""|---
          |${write(frontMatter, indent = 2)}
          |---
          |
          |# $title
          |
          |Contents goes here.
          |""".stripMargin

    val postPath = os.pwd / ProjectPaths.Content.path / ProjectPaths.Posts.path / s"${slug.value}.md"
    if os.exists(postPath) then
      println(s"Error: Post ${postPath.last} already exists")
    else
      os.write(postPath, content)
      println(s"Created post: ${postPath.relativeTo(os.pwd)}")

  @main
  def build(@arg(doc = "Include drafts") drafts: Boolean = false,
            @arg(doc = "Concurrency level") concurrency: Int = 1): Unit =
    val configPath = os.pwd / ProjectPaths.Config.path
    if !os.exists(configPath) then
      println(s"Error: ${ProjectPaths.Config.path} not found. Run 'init' first.")
      sys.exit(1)

    val config = read[SiteConfig](os.read(configPath))
    val publicDir = os.pwd / ProjectPaths.Public.path
    os.remove.all(publicDir)
    os.makeDir.all(publicDir / ProjectPaths.Posts.path)
    os.makeDir.all(publicDir / ProjectPaths.Tags.path)

    val postsDir = os.pwd / ProjectPaths.Content.path / ProjectPaths.Posts.path
    if !os.exists(postsDir) then
      os.makeDir.all(postsDir)

    val errors = collection.mutable.ListBuffer.empty[String]
    val warnings = collection.mutable.ListBuffer.empty[String]

    import scala.concurrent.{Await, Future, ExecutionContext}
    import java.util.concurrent.Executors
    val executor = Executors.newFixedThreadPool(concurrency)
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

    val postFiles = os.list(postsDir).filter(_.ext == "md")
    val allParsedPostsFutures = postFiles.map { path =>
      Future {
        try {
          val content = os.read(path)
          val (fm, body) = Markdown.parseFile(content)
          
          if fm.title == "Error Parsing Front Matter" then
            synchronized { warnings += s"Failed to parse front matter in ${path.last}" }

          val finalBody = if config.plugins.contains("Shortcodes") then
            Plugins.applyShortcodes(body)
          else
            body

          val html = Markdown.render(finalBody)
          val slug = Slugs.Slug(fm.title).value
          Some((fm, html, slug, body))
        } catch {
          case e: Exception =>
            synchronized { errors += s"Error processing ${path.last}: ${e.getMessage}" }
            None
        }
      }
    }
    
    import scala.concurrent.duration.Duration
    val allParsedPosts = Await.result(Future.sequence(allParsedPostsFutures), Duration.Inf).flatten.toList
    executor.shutdown()

    val filteredPosts = if drafts then allParsedPosts else allParsedPosts.filterNot(_._1.draft)

    // Generate post pages
    filteredPosts.foreach { (fm, html, slug, _) =>
      val postHtml = Templates.post(fm, html, config.title)
      os.write(publicDir / ProjectPaths.Posts.path / s"$slug.html", postHtml)
      println(s"Built post: $slug.html")
    }

    // Generate index page
    val indexHtml = Templates.index(
      filteredPosts.map((fm, _, slug, _) => (fm, slug)),
      config.title,
      config.description
    )
    os.write(publicDir / ProjectPaths.IndexHtml.path, indexHtml)
    println(s"Built ${ProjectPaths.IndexHtml.path}")

    // Generate tag pages
    val allTags = filteredPosts.flatMap(_._1.tags).distinct
    allTags.foreach { tag =>
      val taggedPosts = filteredPosts.filter(_._1.tags.contains(tag)).map((fm, _, slug, _) => (fm, slug))
      val tagHtml = Templates.tagPage(tag, taggedPosts, config.title)
      os.write(publicDir / ProjectPaths.Tags.path / s"$tag.html", tagHtml)
      println(s"Built tag page: $tag.html")
    }

    // Generate search page
    val searchHtml = Templates.search(config.title)
    os.write(publicDir / ProjectPaths.SearchHtml.path, searchHtml)
    println(s"Built ${ProjectPaths.SearchHtml.path}")

    // Plugins
    if config.plugins.contains("Sitemap") then
      val sitemap = Plugins.generateSitemap(config.baseUrl, filteredPosts.map(_._3))
      os.write(publicDir / ProjectPaths.SitemapXml.path, sitemap)
      println(s"Generated ${ProjectPaths.SitemapXml.path}")

    if config.plugins.contains("SearchIndex") then
      val searchIndex = Plugins.generateSearchIndex(filteredPosts.map((fm, _, slug, body) => (fm, body, slug)))
      os.write(publicDir / ProjectPaths.SearchIndexJson.path, searchIndex)
      println(s"Generated ${ProjectPaths.SearchIndexJson.path}")

    // Build Report
    val report = Plugins.generateBuildReport(errors.toList, warnings.toList)
    os.write(publicDir / ProjectPaths.BuildReportJson.path, report)
    println(s"Generated ${ProjectPaths.BuildReportJson.path}")

    // Copy assets
    val assetsDir = os.pwd / ProjectPaths.Assets.path
    if os.exists(assetsDir) then
      os.copy(assetsDir, publicDir / ProjectPaths.Assets.path, mergeFolders = true)
      println("Copied assets.")

    if errors.nonEmpty then
      println(s"Build finished with ${errors.size} errors and ${warnings.size} warnings.")
      sys.exit(1)
    else
      println(s"Build completed successfully with ${warnings.size} warnings.")

  @main
  def serve(@arg(doc = "Port to serve on") port: Int = 8080,
            @arg(doc = "Watch for changes") watch: Boolean = false): Unit =
    val publicDir = os.pwd / ProjectPaths.Public.path
    if !os.exists(publicDir) then
      println("Building site first...")
      build()

    println(s"Starting server on http://localhost:$port")
    val serverObj = new Server(publicDir, port)
    if watch then
      println("Watching for changes...")
      val thread = new Thread(() => {
        var lastMTime = Map.empty[os.Path, Long]
        def getMTimes = os.walk(os.pwd / ProjectPaths.Content.path).map(p => p -> os.mtime(p)).toMap
        lastMTime = getMTimes
        while (true) {
          Thread.sleep(1000)
          val currentMTimes = getMTimes
          if (currentMTimes != lastMTime) {
            println("Change detected, rebuilding...")
            try {
              build()
              serverObj.notifyReload()
              lastMTime = currentMTimes
            } catch {
              case e: Exception => println(s"Build failed: ${e.getMessage}")
            }
          }
        }
      })
      thread.setDaemon(true)
      thread.start()

    val app = new cask.main.Main:
      override def allRoutes = Seq(serverObj)
      override def port: Int = serverObj.overridePort
      override def host: String = "0.0.0.0"
    app.main(Array())

@main
def entry(args: String*): Unit =
  ParserForMethods(Commands).runOrExit(args)

object MainApp:
  def main(args: Array[String]): Unit =
    entry(args*)
