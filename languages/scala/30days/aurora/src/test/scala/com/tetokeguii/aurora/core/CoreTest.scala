package com.tetokeguii.aurora.core

class CoreTest extends munit.FunSuite:
  test("Markdown parsing") {
    val content =
      """|---
         |{
         |  "title": "Test Post",
         |  "date": "2023-01-01",
         |  "tags": ["test"],
         |  "draft": false
         |}
         |---
         |# Header
         |Body content.
         |""".stripMargin
    val (fm, body) = Markdown.parseFile(content)
    assertEquals(fm.title, "Test Post")
    assertEquals(fm.date, "2023-01-01")
    assertEquals(fm.tags, List("test"))
    assertEquals(fm.draft, false)
    assert(body.contains("# Header"))
    assert(body.contains("Body content."))
  }

  test("Markdown rendering") {
    val body = "# Hello\nThis is **bold**."
    val html = Markdown.render(body)
    assertEquals(html.trim, "<h1>Hello</h1>\n<p>This is <strong>bold</strong>.</p>")
  }

  test("Shortcodes") {
    val content = "Check this video: {{ youtube id=\"dQw4w9WgXcQ\" }}"
    val result = Plugins.applyShortcodes(content)
    assert(result.contains("https://www.youtube.com/embed/dQw4w9WgXcQ"))
  }
