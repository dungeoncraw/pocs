package beans

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("singleton")
class SingletonBean(private val clock: java.time.Clock):
  println(s"SingletonBean created: ${this.hashCode()}")

  def nowIso: String =
    java.time.Instant.now(clock).toString

@Component
@Scope("prototype")
class PrototypeBean(private val clock: java.time.Clock):
  println(s"PrototypeBean created: ${this.hashCode()}")

  def nowIso: String =
    java.time.Instant.now(clock).toString