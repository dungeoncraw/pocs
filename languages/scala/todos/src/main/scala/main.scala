package com.tetokeguii

import com.tetokeguii.modules.routes.Routes
import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "todo-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route = Routes.get_routes()

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
