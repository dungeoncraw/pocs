import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import scala.concurrent.ExecutionContext
import scala.io.StdIn

@main
def main(): Unit = {
  given system: ActorSystem = ActorSystem("pekko-server")
  given ec: ExecutionContext = system.dispatcher

  val binding = Http().newServerAt("0.0.0.0", 8080).bind(Routes.route)

  println("Server started at http://localhost:8080/")
  println("Press ENTER to stop...")
  StdIn.readLine()

  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}