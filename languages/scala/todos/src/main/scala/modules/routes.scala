package com.tetokeguii.modules.routes

import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route


object Routes {
  private lazy val topLevelRoute: Route =
    // provide top-level path structure here but delegate functionality to subroutes for readability
    concat(
      path("todo")(todosRoute),
    )

  private val todosRoute: Route =
    concat(
      get {
        // TODO: get the todo class and marshall to JSON
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Would return the todoList</h1>"))
      },
      post {
        // TODO: validate and store the TODO
        complete {
          "Todo received"
        }
      })
  def get_routes(): Route = {
    topLevelRoute
  }
}