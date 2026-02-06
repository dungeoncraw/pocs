package com.induction.sdk

import org.scalatest.funsuite.AnyFunSuite
import sttp.client3.*

class InductionSDKTest extends AnyFunSuite:

  class MockFetcher(profiles: Map[String, Profile]) extends ProfileFetcher:
    override def fetch(profileId: String): Option[Profile] = profiles.get(profileId)

  test("InductionSDK should return mock response when MockWhole action is specified") {
    val profileId = "test-profile-1"
    val mockResponse = """{"status": "mocked"}"""
    val profile = Profile(
      id = profileId,
      action = Action.MockWhole,
      mockResponse = Some(mockResponse)
    )

    val sdk = new InductionSDK(new MockFetcher(Map(profileId -> profile)))

    val request = basicRequest
      .get(uri"http://example.com/api/data")
      .header("X-Induction-Profile-ID", profileId)

    val response = sdk.instrumentRequest(request)

    assert(response.body == Right(mockResponse))
    assert(response.code.isSuccess)
  }

  test("InductionSDK should throw exception when Exception action is specified") {
    val profileId = "test-profile-exc"
    val profile = Profile(
      id = profileId,
      action = Action.Exception,
      exceptionMessage = Some("Boom!")
    )

    val sdk = new InductionSDK(new MockFetcher(Map(profileId -> profile)))

    val request = basicRequest
      .get(uri"http://example.com/api/data")
      .header("X-Induction-Profile-ID", profileId)

    val exception = intercept[RuntimeException] {
      sdk.instrumentRequest(request)
    }
    assert(exception.getMessage == "Boom!")
  }

  test("InductionSDK should mutate JSON response when Mutate action is specified") {
    val profileId = "test-profile-mutate"
    val profile = Profile(
      id = profileId,
      action = Action.Mutate,
      mutations = List(Transformation("status", "MUTATED"))
    )

    val initialBody = """{"status": "original", "other": "data"}"""
    val testingBackend = sttp.client3.testing.SttpBackendStub.synchronous
      .whenAnyRequest
      .thenRespond(initialBody)

    val sdk = new InductionSDK(new MockFetcher(Map(profileId -> profile)), testingBackend)

    val request = basicRequest
      .get(uri"http://example.com/api/data")
      .header("X-Induction-Profile-ID", profileId)

    val response = sdk.instrumentRequest(request)
    val responseBody = response.body.getOrElse("")
    
    assert(responseBody.contains(""""status":"MUTATED""""))
    assert(responseBody.contains(""""other":"data""""))
  }
