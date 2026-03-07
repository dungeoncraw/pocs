
import org.springframework.context.annotation.{AnnotationConfigApplicationContext, ComponentScan, Configuration, PropertySource}
import com.example.ValueExample

@Configuration
@ComponentScan(basePackages = Array("com.example"))
@PropertySource(Array("classpath:application.properties"))
class AppConfig

@main
def main(): Unit = {
  val context = new AnnotationConfigApplicationContext(classOf[AppConfig])
  val app = context.getBean(classOf[ValueExample])
  app.display()
  context.close()
}

