import java.time.{Duration, Instant}
import timemachine.*
import timemachine.Instances.given

@main
def main(): Unit =
  val now = Instant.now()
  val twoHours = Duration.ofHours(2)
  val backOneDay = Duration.ofDays(-1)

  val future1 = timeTravel(now, twoHours)
  println(s"Future via function: ${future1.shown}")

  val past1 = now.jump(backOneDay)
  println(s"Past via extension:  ${past1.shown}")

  val dict: TimeMachine[Instant] = summon[TimeMachine[Instant]]
  val future2 = timeTravel(now, twoHours)(using dict)
  println(s"Future explicit dict: ${future2.shown}")

  val past2 = dict.jump(now, backOneDay)
  println(s"Past direct dict:    ${past2.shown}")