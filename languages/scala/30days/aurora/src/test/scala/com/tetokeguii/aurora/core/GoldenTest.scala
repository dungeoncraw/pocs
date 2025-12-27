package com.tetokeguii.aurora.core

class GoldenTest extends munit.FunSuite:
  test("Golden test: post rendering") {
    val fm = FrontMatter("Golden Post", "2023-12-25", List("tag1"), false)
    val body = "This is a **golden** post."
    val htmlContent = Markdown.render(body)
    val rendered = Templates.post(fm, htmlContent, "My Site")
    
    // In a real golden test, we would load this from a file
    // For now, we'll just check some key elements
    assert(rendered.contains("<!DOCTYPE html>"))
    assert(rendered.contains("<title>Golden Post | My Site</title>"))
    assert(rendered.contains("<h2>Golden Post</h2>"))
    assert(rendered.contains("<strong>golden</strong>"))
  }
