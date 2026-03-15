package user

import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import scala.jdk.CollectionConverters._

@RestController
@RequestMapping(Array("/users"))
class UserController(val userRepository: UserRepository, val restTemplate: RestTemplate):

  @GetMapping
  def getAll: java.util.List[User] =
    userRepository.findAll().asScala.toList.asJava

  @GetMapping(Array("/{id}"))
  def getById(@PathVariable id: Long): User =
    userRepository.findById(id).orElseThrow()

  @PostMapping
  def create(@RequestBody user: User): User =
    userRepository.save(user)

  @GetMapping(Array("/external"))
  def getExternalData: String =
    restTemplate.getForObject("https://api.github.com", classOf[String])
