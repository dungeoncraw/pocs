package com.induction.sdk

import sttp.client3.*
import upickle.default.*
import java.nio.file.{Files, Paths}

trait ProfileFetcher:
  def fetch(profileId: String): Option[Profile]

class HttpProfileFetcher(serverUrl: String) extends ProfileFetcher:
  private val backend = HttpClientSyncBackend()

  override def fetch(profileId: String): Option[Profile] =
    val request = basicRequest
      .get(uri"$serverUrl/profiles/$profileId")
      .response(asStringAlways)

    val response = request.send(backend)
    if response.code.isSuccess then
      Some(read[Profile](response.body))
    else
      None

class FileProfileFetcher(basePath: String) extends ProfileFetcher:
  override def fetch(profileId: String): Option[Profile] =
    val path = Paths.get(basePath, s"$profileId.json")
    if Files.exists(path) then
      val content = Files.readString(path)
      Some(read[Profile](content))
    else
      None

class CompositeProfileFetcher(fetchers: List[ProfileFetcher]) extends ProfileFetcher:
  override def fetch(profileId: String): Option[Profile] =
    fetchers.view.flatMap(_.fetch(profileId)).headOption

object Environment:
  def isProduction: Boolean =
    sys.env.get("APP_ENV").contains("production")
