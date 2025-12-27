package com.tetokeguii.aurora.core

import upickle.default.*

case class SearchItem(title: String, slug: String, content: String) derives ReadWriter

object Plugins:
  def applyShortcodes(content: String): String =
    // Example shortcode: {{ youtube id="123" }}
    val youtubeRegex = """\{\{\s*youtube\s+id="([^"]+)"\s*\}\}""".r
    youtubeRegex.replaceAllIn(content, m =>
      s"""<div class="video-container"><iframe src="https://www.youtube.com/embed/${m.group(1)}" frameborder="0" allowfullscreen></iframe></div>"""
    )

  def generateSitemap(baseUrl: String, posts: List[String]): String =
    val urls = ("" :: posts.map(s => s"${ProjectPaths.Posts.path}/$s.html")).map(p => s"$baseUrl/$p")
    s"""|<?xml version="1.0" encoding="UTF-8"?>
        |<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        |${urls.map(url => s"  <url><loc>$url</loc></url>").mkString("\n")}
        |</urlset>""".stripMargin

  def generateSearchIndex(posts: List[(FrontMatter, String, String)]): String =
    val items = posts.map((fm, body, slug) => SearchItem(fm.title, slug, body.take(200)))
    write(items, indent = 2)

  def generateBuildReport(errors: List[String], warnings: List[String]): String =
    val report = Map(
      "errors" -> write(errors),
      "warnings" -> write(warnings),
      "timestamp" -> java.time.LocalDateTime.now().toString
    )
    write(report, indent = 2)
