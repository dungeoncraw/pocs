package com.tetokeguii.aurora.core

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.ext.gfm.tables.TablesExtension
import java.util.Arrays
import upickle.default.*
import scala.util.Try

object Markdown:
  private val parser = Parser.builder()
    .extensions(Arrays.asList(TablesExtension.create()))
    .build()
  private val renderer = HtmlRenderer.builder()
    .extensions(Arrays.asList(TablesExtension.create()))
    .build()

  def render(markdown: String): String =
    val document = parser.parse(markdown)
    renderer.render(document)

  def parseFile(content: String): (FrontMatter, String) =
    val parts = content.split("---", 3)
    if parts.length < 3 then
      (FrontMatter("Untitled", "1970-01-01"), content)
    else
      val fmJson = parts(1).trim
      val body = parts(2).trim
      val fm = Try(read[FrontMatter](fmJson)).getOrElse(FrontMatter("Error Parsing Front Matter", "1970-01-01"))
      (fm, body)
