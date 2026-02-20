package timemachine

import java.time.{Duration, Instant, ZoneOffset}
import java.time.format.DateTimeFormatter

object Instances:
  given TimeMachine[Instant] with
    def jump(i: Instant, by: Duration): Instant =
      i.plus(by)

  given Show[Instant] with
    def show(i: Instant): String =
      DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(i)
