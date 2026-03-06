import com.example.AppConfig
import org.springframework.context.annotation.AnnotationConfigApplicationContext

@main
def main(): Unit =
  val context = new AnnotationConfigApplicationContext(classOf[AppConfig])
  context.close()

