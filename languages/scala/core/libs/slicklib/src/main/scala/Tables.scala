package com.example.slicklib

import slick.jdbc.JdbcProfile

class Tables(val profile: JdbcProfile) {
  import profile.api.*

  class Users(tag: Tag) extends Table[(Int, String, String)](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    private def email = column[String]("email")
    def * = (id, name, email)
  }
  val users = TableQuery[Users]

  class Posts(tag: Tag) extends Table[(Int, String, Int, Int)](tag, "posts") {
    private def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    private def title = column[String]("title")
    def authorId = column[Int]("author_id")
    def rating = column[Int]("rating")
    def * = (id, title, authorId, rating)
  }
  val posts = TableQuery[Posts]

  def complexQuery = {
    (for {
      (u, p) <- users join posts on (_.id === _.authorId)
    } yield (u, p))
      .groupBy(_._1.name)
      .map { case (name, group) =>
        (name, group.map(_._2.rating).avg, group.length)
      }
      .filter { case (name, avgRating, count) => avgRating.getOrElse(0) >= 4 }
  }
}
