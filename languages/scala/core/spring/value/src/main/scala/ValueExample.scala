package com.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Component
class ValueExample {

  @Value("${app.name}")
  @BeanProperty
  var appName: String = uninitialized

  @Value("${app.missing:Default Name}")
  @BeanProperty
  var missingProperty: String = uninitialized

  // SpEL (Spring Expression Language)
  @Value("#{ 'Literal Value' }")
  @BeanProperty
  var literalValue: String = uninitialized

  @Value("#{ 2 * 10 + 5 }")
  @BeanProperty
  var calculatedValue: Int = uninitialized

  @Value("#{ systemProperties['user.home'] }")
  @BeanProperty
  var userHome: String = uninitialized

  @Value("${app.tags}")
  @BeanProperty
  var tags: Array[String] = uninitialized

  @Value("${app.numbers}")
  @BeanProperty
  var numbers: Array[Int] = uninitialized

  @Value("#{ ${app.config} }")
  @BeanProperty
  var appConfig: java.util.Map[String, String] = uninitialized

  @Value("#{ '${app.version}' ?: 'unknown' }")
  @BeanProperty
  var appVersion: String = uninitialized

  def display(): Unit = {
    println(s"1. Simple Property (app.name): $appName")
    println(s"2. Default Value (app.missing): $missingProperty")
    println(s"3. SpEL Literal: $literalValue")
    println(s"4. SpEL (2 * 10 + 5): $calculatedValue")
    println(s"5. SpEL (user.home): $userHome")
    println(s"6. Lists/Arrays (app.tags): ${tags.mkString(", ")}")
    println(s"   app.numbers): ${numbers.mkString(", ")}")
    println(s"7. SpEL (app.config): $appConfig")
    println(s"8. SpEL (app.version): $appVersion")
  }
}
