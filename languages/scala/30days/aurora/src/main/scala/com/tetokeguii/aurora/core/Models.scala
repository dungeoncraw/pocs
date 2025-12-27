package com.tetokeguii.aurora.core

import upickle.default.*

case class SiteConfig(
    title: String,
    description: String,
    baseUrl: String,
    plugins: List[String] = List("Shortcodes", "Sitemap", "SearchIndex")
) derives ReadWriter

case class FrontMatter(
    title: String,
    date: String,
    tags: List[String] = Nil,
    draft: Boolean = false
) derives ReadWriter
