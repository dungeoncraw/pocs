package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

case class Item(id: Long = 0, name: String = "", description: String = "")

@SpringBootApplication
class Application {
  @Bean
  def scalaModule(): DefaultScalaModule = DefaultScalaModule
}

@RestController
class HelloController(@Value("${app.message:Hello World}") message: String) {

  private val items = mutable.Map[Long, Item](
    1L -> Item(1L, "Initial Item", "This is an initial item"),
    2L -> Item(2L, "Second Item", "This is the second item")
  )

  @GetMapping(Array("/hello"))
  def hello(): String = message

  @GetMapping(Array("/items"))
  def getItems: java.util.List[Item] = items.values.toList.asJava

  @GetMapping(Array("/items/{id}"))
  def getItem(@PathVariable id: Long): Item = items.getOrElse(id, throw new RuntimeException("Item not found"))

  @PostMapping(Array("/items"))
  def createItem(@RequestBody item: Item): Item = {
    val id = if (items.isEmpty) 1L else items.keys.max + 1
    val newItem = item.copy(id = id)
    items += (id -> newItem)
    newItem
  }

  @PutMapping(Array("/items/{id}"))
  def updateItem(@PathVariable id: Long, @RequestBody item: Item): Item = {
    if (!items.contains(id)) throw new RuntimeException("Item not found")
    val updatedItem = item.copy(id = id)
    items += (id -> updatedItem)
    updatedItem
  }

  @DeleteMapping(Array("/items/{id}"))
  def deleteItem(@PathVariable id: Long): String = {
    items -= id
    s"Item $id deleted"
  }

  @GetMapping(Array("/sum-prime"))
  def heavyProcess(@RequestParam(defaultValue = "1000") n: Int): String = {
    // Some "heavy" processing: calculating sum of first n primes (very naive)
    def isPrime(k: Int): Boolean = {
      if (k <= 1) false
      else if (k <= 3) true
      else if (k % 2 == 0 || k % 3 == 0) false
      else {
        var i = 5
        var result = true
        while (i * i <= k && result) {
          if (k % i == 0 || k % (i + 2) == 0) result = false
          i += 6
        }
        result
      }
    }

    val sum = (1 to n).filter(isPrime).sum
    s"Sum of primes up to $n is $sum"
  }
}

@main
def main(args: String*): Unit = {
  SpringApplication.run(classOf[Application], args*)
}
