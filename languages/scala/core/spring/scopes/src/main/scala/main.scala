
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import config.ProjectConfig
import beans.{SingletonBean, PrototypeBean}

@main
def main(): Unit = {
  println("--- Initializing Spring Context ---")
  val context = new AnnotationConfigApplicationContext(classOf[ProjectConfig])
  
  println("\n--- Requesting Singleton Bean ---")
  val s1 = context.getBean(classOf[SingletonBean])
  val s2 = context.getBean(classOf[SingletonBean])
  println(s"s1 == s2: ${s1 == s2}")
  println(s"s1.nowIso = ${s1.nowIso}")
  println(s"s2.nowIso = ${s2.nowIso}")

  println("\n--- Requesting Prototype Bean ---")
  val p1 = context.getBean(classOf[PrototypeBean])
  val p2 = context.getBean(classOf[PrototypeBean])
  println(s"p1 == p2: ${p1 == p2}")
  
  context.close()
}

