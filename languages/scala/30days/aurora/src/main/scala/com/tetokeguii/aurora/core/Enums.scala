package com.tetokeguii.aurora.core

enum ProjectPaths(val path: String):
  case Config extends ProjectPaths("config.json")
  case Content extends ProjectPaths("content")
  case Posts extends ProjectPaths("posts")
  case Templates extends ProjectPaths("templates")
  case Assets extends ProjectPaths("assets")
  case Public extends ProjectPaths("public")
  case IndexHtml extends ProjectPaths("index.html")
  case SitemapXml extends ProjectPaths("sitemap.xml")
  case SearchIndexJson extends ProjectPaths("search-index.json")
  case BuildReportJson extends ProjectPaths("build-report.json")
  case Tags extends ProjectPaths("tags")
  case SearchHtml extends ProjectPaths("search.html")