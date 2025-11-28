package com.tetokeguii.day21
import io.circe.*
import io.circe.generic.auto.*

import java.nio.file.{Files, Paths}
import scala.util.Try

//This is a bit of a hack, the keys must be the same as the csv headers, otherwise it won't work
//Another option would be to normalize the csv headers to match class fields
case class User(Index: Int, CustomerId: String, FirstName: String, LastName: String, Company: String, City: String, Country: String, Phone1: String, Phone2: String, Email: String, SubscriptionDate: String, Website: String)

def parseCsvToUsers(csv: String): List[Either[Error, User]] = {
  val lines = csv.split("\n").toList.map(_.trim).filter(_.nonEmpty)

  if (lines.isEmpty) return List.empty

  val headers = lines.head.split(",").map(_.trim)

  lines.tail.map { line =>
    val values = line.split(",").map(_.trim)

    val dataMap = headers.zip(values).toMap
    val jsonObject = Json.obj(
      dataMap.map { case (k, v) =>
        k -> convertToJsonValue(v)
      }.toSeq: _*
    )
    jsonObject.as[User]
  }
}

def convertToJsonValue(value: String): Json = {
  if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
    Json.fromBoolean(value.toBoolean)
  } else if (value.forall(_.isDigit)) {
    Try(Json.fromInt(value.toInt))
      .orElse(Try(Json.fromLong(value.toLong)))
      .getOrElse(Json.fromString(value))
  } else {
    Json.fromString(value)
  }
}
@main
def main(): Unit = {
  val input = Files.readString(Paths.get("src/main/resources/customers-100.csv"))
  val users = parseCsvToUsers(input)
  users.foreach{
    case Right(user) => println(s"Success user: $user")
    case Left(error) => println(s"Failed to parse: $error")
  }
}

