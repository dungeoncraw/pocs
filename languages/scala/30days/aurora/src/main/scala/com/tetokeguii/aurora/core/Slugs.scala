package com.tetokeguii.aurora.core

import upickle.default.*

object Slugs:
  opaque type Slug = String

  object Slug:
    def apply(s: String): Slug =
      s.toLowerCase
        .replaceAll("[^a-z0-9]", "-")
        .replaceAll("-+", "-")
        .stripPrefix("-")
        .stripSuffix("-")
    // include the option for getting the value of opaque slug outside of the object
    extension (s: Slug)
      def value: String = s

    given ReadWriter[Slug] = readwriter[String].bimap(_.value, Slug.apply)

  opaque type UrlPath = String

  object UrlPath:
    def apply(s: String): UrlPath =
      if s.startsWith("/") then s else s"/$s"

    extension (u: UrlPath)
      def value: String = u

    given ReadWriter[UrlPath] = readwriter[String].bimap(_.value, UrlPath.apply)
