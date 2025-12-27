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
