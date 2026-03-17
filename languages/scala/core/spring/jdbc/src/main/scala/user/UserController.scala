package user

import org.springframework.cache.annotation.{CacheEvict, CachePut, Cacheable}
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

import scala.jdk.CollectionConverters.*

@RestController
@RequestMapping(Array("/users"))
class UserController(val userRepository: UserRepository, val restTemplate: RestTemplate):

  @GetMapping
  @Cacheable(value = Array("users"))
  def getAll: java.util.List[User] =
    userRepository.findAll().asScala.toList.asJava

  @GetMapping(Array("/{id}"))
  @Cacheable(value = Array("userById"), key = "#id")
  def getById(@PathVariable id: Long): User =
    userRepository.findById(id).orElseThrow()

  @PostMapping
  @CachePut(value = Array("userById"), key = "#result.id")
  @CacheEvict(value = Array("users"))
  def create(@RequestBody user: User): User =
    userRepository.save(user)

  @GetMapping(Array("/external"))
  @Cacheable(value = Array("externalData"))
  def getExternalData: String =
    restTemplate.getForObject("https://api.github.com", classOf[String])
