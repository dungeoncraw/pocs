package timemachine

trait Show[A]:
  def show(a: A): String
