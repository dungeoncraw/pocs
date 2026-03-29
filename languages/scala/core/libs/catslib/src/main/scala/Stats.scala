import cats.*

case class Stats(count: Int, sum: Double, avg: Double)

given Semigroup[Stats] with
  def combine(x: Stats, y: Stats): Stats = Stats(x.count + y.count, x.sum + y.sum, (x.sum + y.sum) / (x.count + y.count))