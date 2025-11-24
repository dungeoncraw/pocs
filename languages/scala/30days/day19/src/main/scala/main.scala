package com.tetokeguii.day19
import java.nio.file.{Files, Paths}


@main
def main(): Unit = {
  val input = Files.readString(Paths.get("src/main/resources/customers-100.csv"))
  val lines = input.split("\\R").toList
  val header = lines.headOption
  val data = lines.drop(1)
  val parsedData = data.map { row => row.split(",").map(_.trim)}
  parsedData.take(2).foreach{ col => println(col.mkString("|"))}
  println(parsedData.length)
}

