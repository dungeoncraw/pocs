package com.tetokeguii.aurora.core

import munit.FunSuite

class MarkdownTest extends FunSuite:
  test("Markdown.render should convert markdown to HTML") {
    val markdown = "# Hello\nThis is **bold**."
    val expected = "<h1>Hello</h1>\n<p>This is <strong>bold</strong>.</p>\n"
    assertEquals(Markdown.render(markdown), expected)
  }

  test("Markdown.render should support tables") {
    val markdown = """| Header |
                     || --- |
                     || Cell |""".stripMargin
    val rendered = Markdown.render(markdown)
    assert(rendered.contains("<table>"))
    assert(rendered.contains("<thead>"))
    assert(rendered.contains("<th>Header</th>"))
    assert(rendered.contains("<td>Cell</td>"))
  }

  test("Markdown.parseFile should extract front matter and body") {
    val content = """|---
                     |{
                     |  "title": "My Post",
                     |  "date": "2023-10-01",
                     |  "tags": ["scala", "test"]
                     |}
                     |---
                     |# The Body""".stripMargin
    val (fm, body) = Markdown.parseFile(content)
    assertEquals(fm.title, "My Post")
    assertEquals(fm.date, "2023-10-01")
    assertEquals(fm.tags, List("scala", "test"))
    assertEquals(body, "# The Body")
  }

  test("Markdown.parseFile should handle missing front matter") {
    val content = "# Just Body"
    val (fm, body) = Markdown.parseFile(content)
    assertEquals(fm.title, "Untitled")
    assertEquals(body, "# Just Body")
  }

  test("Markdown.parseFile should handle invalid front matter") {
    val content = """|---
                     |invalid json
                     |---
                     |# Body""".stripMargin
    val (fm, body) = Markdown.parseFile(content)
    assertEquals(fm.title, "Error Parsing Front Matter")
    assertEquals(body, "# Body")
  }
