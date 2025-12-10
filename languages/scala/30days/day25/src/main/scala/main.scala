import scala.jdk.CollectionConverters.*
import scala.jdk.javaapi.OptionConverters.*

@main
def main(): Unit = {
  // creates a scala.collection.immutable.List[Int] from a java.util.ArrayList[Int]
  val jlist = new java.util.ArrayList[Int]()
  jlist.add(1);
  println(jlist.asScala.map(_ + 1))
  val o: Option[String] = Some("x")
  // wrap the scala.Option[String] in a java.util.Optional[String]
  val j: java.util.Optional[String] = toJava(o)
  println(j.get())
}

