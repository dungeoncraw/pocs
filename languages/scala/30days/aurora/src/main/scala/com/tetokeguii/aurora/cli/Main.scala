package com.tetokeguii.aurora.cli

import mainargs.*
import com.tetokeguii.aurora.core.*
import com.tetokeguii.aurora.core.Slugs.*
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
    val configPath = os.pwd / "config.json"
    if os.exists(configPath) then
      println("Error: config.json already exists")
    else
      os.write(configPath, write(config, indent = 2))
      os.makeDir.all(os.pwd / "content" / "posts")
      os.makeDir.all(os.pwd / "templates")
      os.makeDir.all(os.pwd / "assets")
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

    val postPath = os.pwd / "content" / "posts" / s"${slug.value}.md"
    if os.exists(postPath) then
      println(s"Error: Post ${postPath.last} already exists")
    else
      os.write(postPath, content)
      println(s"Created post: ${postPath.relativeTo(os.pwd)}")

  @main
  def build(): Unit =
    println("Build command not implemented.")

@main
def entry(args: String*): Unit =
  ParserForMethods(Commands).runOrExit(args)

object MainApp:
  def main(args: Array[String]): Unit =
    entry(args*)
