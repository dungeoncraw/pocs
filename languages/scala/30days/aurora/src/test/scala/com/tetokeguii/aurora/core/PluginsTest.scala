package com.tetokeguii.aurora.core

import munit.FunSuite
import upickle.default.*

class PluginsTest extends FunSuite:
  test("Plugins.applyShortcodes should replace youtube shortcode") {
    val content = "Watch this: {{ youtube id=\"dQw4w9WgXcQ\" }}"
    val expected = "Watch this: <div class=\"video-container\"><iframe src=\"https://www.youtube.com/embed/dQw4w9WgXcQ\" frameborder=\"0\" allowfullscreen></iframe></div>"
    assertEquals(Plugins.applyShortcodes(content), expected)
  }

  test("Plugins.generateSitemap should create a valid XML sitemap") {
    val baseUrl = "https://example.com"
    val posts = List("hello-world", "my-second-post")
    val sitemap = Plugins.generateSitemap(baseUrl, posts)
    
    assert(sitemap.contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"))
    assert(sitemap.contains("<loc>https://example.com/</loc>"))
    assert(sitemap.contains("<loc>https://example.com/posts/hello-world.html</loc>"))
    assert(sitemap.contains("<loc>https://example.com/posts/my-second-post.html</loc>"))
  }

  test("Plugins.generateSearchIndex should create a JSON index of posts") {
    val posts = List(
      (FrontMatter("Title 1", "2023-01-01"), "Body of first post", "slug-1"),
      (FrontMatter("Title 2", "2023-01-02"), "Body of second post which is a bit longer", "slug-2")
    )
    val searchIndexJson = Plugins.generateSearchIndex(posts)
    val items = read[List[SearchItem]](searchIndexJson)
    
    assertEquals(items.size, 2)
    assertEquals(items(0).title, "Title 1")
    assertEquals(items(0).slug, "slug-1")
    assertEquals(items(1).title, "Title 2")
    assertEquals(items(1).slug, "slug-2")
  }

  test("Plugins.generateBuildReport should contain errors and warnings") {
    val errors = List("Error 1")
    val warnings = List("Warning 1", "Warning 2")
    val reportJson = Plugins.generateBuildReport(errors, warnings)
    val report = read[Map[String, String]](reportJson)
    
    assert(report.contains("errors"))
    assert(report.contains("warnings"))
    assert(report.contains("timestamp"))
    
    assertEquals(read[List[String]](report("errors")), errors)
    assertEquals(read[List[String]](report("warnings")), warnings)
  }
