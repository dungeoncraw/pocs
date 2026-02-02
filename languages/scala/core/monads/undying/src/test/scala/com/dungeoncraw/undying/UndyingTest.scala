package com.dungeoncraw.undying

import munit.FunSuite
import scala.concurrent.ExecutionContext
import com.dungeoncraw.undying.Undying.*

class UndyingTest extends FunSuite:
  given ExecutionContext = ExecutionContext.global

  test("performRitual succeeds with 3 valid graves and intensity 6") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-103",
      "intensity" -> "6",
      "moon" -> "blood"
    )
    Undying.performRitual(scroll).map { result =>
      assert(result.isRight)
      result.foreach { zombies =>
        assertEquals(zombies.size, 9)
      }
    }
  }

  test("performRitual fails if less than 3 graves are provided") {
    val scroll = Map(
      "graves" -> "G-101,G-102",
      "intensity" -> "6"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Forbidden("must provide at least 3 graves")))
    }
  }

  test("performRitual fails for intensity out of range (too high)") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-103",
      "intensity" -> "11"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Forbidden("intensity must be 1..10")))
    }
  }

  test("performRitual fails for intensity out of range (too low)") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-103",
      "intensity" -> "0"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Forbidden("intensity must be 1..10")))
    }
  }

  test("performRitual fails if intensity is not an integer") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-103",
      "intensity" -> "high"
    )
    Undying.performRitual(scroll).map {
      case Left(RitualError.BadFormat("intensity", _)) =>
      case result => fail(s"Expected BadFormat for intensity, got $result")
    }
  }

  test("performRitual fails if 'graves' field is missing") {
    val scroll = Map(
      "intensity" -> "5"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Missing("graves")))
    }
  }

  test("performRitual fails if 'intensity' field is missing") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-103"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Missing("intensity")))
    }
  }

  test("performRitual fails if a grave is not found") {
    val scroll = Map(
      "graves" -> "G-101,G-666,G-103",
      "intensity" -> "5"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.GraveNotFound(GraveId("G-666"))))
    }
  }

  test("performRitual fails if warding sigil blocks a weak zombie") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-104",
      "intensity" -> "5"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Forbidden("warding sigil blocked grave G-104")))
    }
  }

  test("performRitual fails if zombie threat is too high (> 22)") {
    val scroll = Map(
      "graves" -> "G-101,G-102,G-107",
      "intensity" -> "10",
      "moon" -> "blood"
    )
    Undying.performRitual(scroll).map { result =>
      assertEquals(result, Left(RitualError.Forbidden("zombie too strong: threat=25")))
    }
  }

  test("performRitualAccumulating collects multiple validation errors") {
    val scroll = Map(
      "intensity" -> "11"
    )
    Undying.performRitualAccumulating(scroll).map {
      case Left(errors) =>
        assert(errors.contains(RitualError.Missing("graves")))
        assert(errors.contains(RitualError.Forbidden("intensity must be 1..10")))
      case result => fail(s"Expected Left with multiple errors, got $result")
    }
  }

  test("performRitualAccumulating collects multiple grave errors") {
    val scroll = Map(
      "graves" -> "G-101,G-666,G-777",
      "intensity" -> "5"
    )
    Undying.performRitualAccumulating(scroll).map {
      case Left(errors) =>
        assert(errors.contains(RitualError.GraveNotFound(GraveId("G-666"))))
        assert(errors.contains(RitualError.GraveNotFound(GraveId("G-777"))))
      case result => fail(s"Expected Left with multiple grave errors, got $result")
    }
  }

  test("performRitualAccumulating collects multiple warding/threat errors") {
    val scroll = Map(
      "graves" -> "G-101,G-104,G-107",
      "intensity" -> "10",
      "moon" -> "blood"
    )
    Undying.performRitualAccumulating(scroll).map {
      case Left(errors) =>
        assertEquals(errors.size, 3)
        errors.foreach { err =>
          assert(err.isInstanceOf[RitualError.Forbidden])
        }
      case result => fail(s"Expected Left with multiple warding/threat errors, got $result")
    }
  }
