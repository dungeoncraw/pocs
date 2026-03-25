import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import scala.concurrent.ExecutionContext
import scala.io.StdIn

@main
def main(): Unit = {
  val rootBehavior = Behaviors.setup[Nothing] { context =>
    val userActor = context.spawn(UserActor(), "UserActor")
    val orderActor = context.spawn(OrderActor(), "OrderActor")
    
    val routes = new Routes(userActor, orderActor)(using context.system)

    given system: ActorSystem[_] = context.system
    given ec: ExecutionContext = system.executionContext

    val binding = Http().newServerAt("0.0.0.0", 8080).bind(routes.route)

    println("Server started at http://localhost:8080/")
    println("Press ENTER to stop...")
    
    Behaviors.empty
  }

  val system = ActorSystem(rootBehavior, "pekko-server")

  StdIn.readLine()
  system.terminate()
}