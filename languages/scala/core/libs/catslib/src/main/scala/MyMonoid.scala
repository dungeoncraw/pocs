import cats.Monoid
import cats.syntax.monoid.*

case class Metrics(count: Int, sum: Double)

given Monoid[Metrics] with
  def empty: Metrics =
    Metrics(0, 0.0)

  def combine(x: Metrics, y: Metrics): Metrics =
    Metrics(
      count = x.count + y.count,
      sum = x.sum + y.sum
    )