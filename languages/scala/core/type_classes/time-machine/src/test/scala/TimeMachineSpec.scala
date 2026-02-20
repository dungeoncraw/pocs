import java.time.{Duration, Instant}
import munit.FunSuite
import timemachine.*
import timemachine.Instances.given

class TimeMachineSpec extends FunSuite:

  test("TimeMachine[Instant] jump forward") {
    val now = Instant.parse("2026-02-19T20:19:00Z")
    val twoHours = Duration.ofHours(2)
    val result = now.jump(twoHours)
    assertEquals(result, Instant.parse("2026-02-19T22:19:00Z"))
  }

  test("TimeMachine[Instant] jump backward") {
    val now = Instant.parse("2026-02-19T20:19:00Z")
    val oneDay = Duration.ofDays(-1)
    val result = now.jump(oneDay)
    assertEquals(result, Instant.parse("2026-02-18T20:19:00Z"))
  }

  test("Show[Instant] format") {
    val now = Instant.parse("2026-02-19T20:19:00Z")
    assertEquals(now.shown, "2026-02-19T20:19:00Z")
  }

  test("timeTravel function") {
    val now = Instant.parse("2026-02-19T20:19:00Z")
    val duration = Duration.ofMinutes(30)
    val result = timeTravel(now, duration)
    assertEquals(result, Instant.parse("2026-02-19T20:49:00Z"))
  }
