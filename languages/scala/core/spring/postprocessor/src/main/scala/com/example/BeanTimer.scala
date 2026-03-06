package com.example

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import scala.collection.mutable

@Component
class BeanTimer extends BeanPostProcessor:
  private val track = mutable.Map.empty[String, Long]

  override def postProcessBeforeInitialization(bean: AnyRef, name: String): AnyRef =
    track(name) = System.nanoTime()
    bean

  override def postProcessAfterInitialization(bean: AnyRef, name: String): AnyRef =
    track.remove(name).foreach { start =>
      val elapsed = (System.nanoTime() - start) / 1_000_000.0
      println(f"init: $name in $elapsed%.2fms")
    }
    bean
