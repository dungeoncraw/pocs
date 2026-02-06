package com.induction.sdk

import sttp.client3.*
import upickle.default.*
import sttp.model.RequestMetadata
import sttp.model.Method
import sttp.model.Uri

case class SimpleRequestMetadata(
    method: Method,
    uri: Uri,
    headers: Seq[sttp.model.Header]
) extends RequestMetadata

class InductionSDK(fetcher: ProfileFetcher, backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()):
  private val HEADER_NAME = "X-Induction-Profile-ID"

  def instrumentRequest[T](request: Request[T, Any]): Response[T] =
    if Environment.isProduction then
      request.send(backend)
    else
      val profileId = request.headers.find(_.name == HEADER_NAME).map(_.value)
      profileId match
        case Some(id) =>
          fetcher.fetch(id) match
            case Some(profile) => applyProfile(request, profile)
            case None => request.send(backend)
        case None =>
          request.send(backend)

  private def applyProfile[T](request: Request[T, Any], profile: Profile): Response[T] =
    profile.action match
      case Action.CallReal =>
        request.send(backend)

      case Action.MockWhole =>
        val body = profile.mockResponse.getOrElse("")
        createMockResponse(request, body)

      case Action.Mutate =>
        val response = request.send(backend)
        response.body match
          case Right(bodyStr: String) =>
            val mutatedBody = applyMutations(bodyStr, profile.mutations)
            response.copy(body = Right(mutatedBody).asInstanceOf[T])
          case _ =>
            println(s"[SDK] Cannot mutate non-string or error body for profile ${profile.id}")
            response

      case Action.Exception =>
        throw new RuntimeException(profile.exceptionMessage.getOrElse("Induced Exception"))

      case Action.Delay =>
        val delay = profile.delayMs.getOrElse(1000L)
        println(s"[SDK] Inducing delay of ${delay}ms")
        Thread.sleep(delay)
        request.send(backend)

  private def createMockResponse[T](request: Request[T, Any], body: String): Response[T] =
    Response(
      body = Right(body).asInstanceOf[T],
      code = sttp.model.StatusCode.Ok,
      statusText = "OK",
      headers = Nil,
      history = Nil,
      request = SimpleRequestMetadata(request.method, request.uri, request.headers)
    )

  private def applyMutations(json: String, mutations: List[Transformation]): String =
    try {
      var data = ujson.read(json)
      mutations.foreach { m =>
        // Simple field replacement. For nested fields, a more complex logic would be needed.
        data(m.field) = m.value
      }
      ujson.write(data)
    } catch {
      case e: Exception =>
        println(s"[SDK] Failed to apply mutations: ${e.getMessage}")
        json
    }
