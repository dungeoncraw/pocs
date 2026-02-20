package timemachine

import java.time.Duration

trait TimeMachine[A]:
  def jump(a: A, by: Duration): A

object TimeMachine:
  def apply[A](using tm: TimeMachine[A]): TimeMachine[A] = tm
