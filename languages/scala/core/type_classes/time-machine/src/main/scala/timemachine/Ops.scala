package timemachine

import java.time.Duration

def timeTravel[A](a: A, by: Duration)(using tm: TimeMachine[A]): A =
  tm.jump(a, by)

extension [A](a: A)(using tm: TimeMachine[A])
  def jump(by: Duration): A =
    tm.jump(a, by)

extension [A](a: A)(using sh: Show[A])
  def shown: String =
    sh.show(a)
