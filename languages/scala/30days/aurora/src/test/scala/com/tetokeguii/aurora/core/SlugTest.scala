package com.tetokeguii.aurora.core

import com.tetokeguii.aurora.core.Slugs.*

class SlugTest extends munit.FunSuite:
  test("slug conversion") {
    assertEquals(Slug("Hello World").value, "hello-world")
    assertEquals(Slug("Hello   World!!!").value, "hello-world")
    assertEquals(Slug("  spaces-and-DASHES-- ").value, "spaces-and-dashes")
    assertEquals(Slug("123 Numbers").value, "123-numbers")
  }

  test("url path normalization") {
    assertEquals(UrlPath("about").value, "/about")
    assertEquals(UrlPath("/contact").value, "/contact")
  }

  test("slug property: lowercase") {
    val inputs = List("Hello World", "SCALA 3", "slug-test")
    inputs.foreach { s =>
      val slug = Slugs.Slug(s).value
      assertEquals(slug, slug.toLowerCase)
    }
  }

  test("slug property: no spaces") {
    val inputs = List("Hello World", "  leading trailing  ", "middle   spaces")
    inputs.foreach { s =>
      val slug = Slugs.Slug(s).value
      assert(!slug.contains(" "))
    }
  }

  test("slug property: idempotent") {
    val inputs = List("Hello World", "!!!", "already-a-slug")
    inputs.foreach { s =>
      val slug1 = Slugs.Slug(s).value
      val slug2 = Slugs.Slug(slug1).value
      assertEquals(slug1, slug2)
    }
  }
