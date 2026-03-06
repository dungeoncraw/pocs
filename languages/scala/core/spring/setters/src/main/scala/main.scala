
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import com.example.{AppConfig, MessageService, ConstructorMessageService}

@main
def main(): Unit = {
  val context = new AnnotationConfigApplicationContext(classOf[AppConfig])
  
  // Setter Injection Example
  val setterService = context.getBean(classOf[MessageService])
  setterService.printMessage()

  setterService.setMessage("Helloxxxxx!")
  setterService.printMessage()
  // Constructor Injection Example
  val constructorService = context.getBean(classOf[ConstructorMessageService])
  constructorService.printMessage()

  context.close()
}

