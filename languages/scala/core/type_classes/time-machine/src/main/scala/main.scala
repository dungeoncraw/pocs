import java.time.{Duration, Instant, ZoneOffset}
import java.time.format.DateTimeFormatter

trait TimeMachine[A]:
  def jump(a: A, by: Duration): A

object TimeMachine:
  def apply[A](using tm: TimeMachine[A]): TimeMachine[A] = tm

object TimeMachineInstances:
  given TimeMachine[Instant] with
    def jump(i: Instant, by: Duration): Instant =
      i.plus(by)

trait Show[A]:
  def show(a: A): String

object ShowInstances:
  given Show[Instant] with
    def show(i: Instant): String =
      DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(i)

def timeTravel[A](a: A, by: Duration)(using tm: TimeMachine[A]): A =
  tm.jump(a, by)

extension [A](a: A)(using tm: TimeMachine[A])
  def jump(by: Duration): A =
    tm.jump(a, by)

extension [A](a: A)(using sh: Show[A])
  def shown: String =
    sh.show(a)


@main
def main(): Unit =
  val now = Instant.now()
  val twoHours = Duration.ofHours(2)
  val backOneDay = Duration.ofDays(-1)

  import TimeMachineInstances.given
  import ShowInstances.given

  val future1 = timeTravel(now, twoHours)
  println(s"Future via function: ${future1.shown}")

  val past1 = now.jump(backOneDay)
  println(s"Past via extension:  ${past1.shown}")

  val dict: TimeMachine[Instant] = summon[TimeMachine[Instant]]
  val future2 = timeTravel(now, twoHours)(using dict)
  println(s"Future explicit dict: ${future2.shown}")

  val past2 = dict.jump(now, backOneDay)
  println(s"Past direct dict:    ${past2.shown}")