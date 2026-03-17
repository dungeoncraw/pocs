package user

import org.springframework.cache.annotation.{CacheEvict, CachePut, Cacheable}
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import scala.jdk.CollectionConverters.*

@RestController
@RequestMapping(Array("/users"))
class UserController(val userService: UserService, val restTemplate: RestTemplate):

  @GetMapping
  @Cacheable(value = Array("users"))
  def getAll: java.util.List[User] =
    userService.findAll()

  @GetMapping(Array("/{id}"))
  @Cacheable(value = Array("userById"), key = "#id")
  def getById(@PathVariable id: Long): User =
    userService.findById(id).orElseThrow()

  @PostMapping
  @CachePut(value = Array("userById"), key = "#result.id")
  @CacheEvict(value = Array("users"))
  def create(@RequestBody user: User): User =
    userService.save(user)

  @GetMapping(Array("/external"))
  @Cacheable(value = Array("externalData"))
  def getExternalData: String =
    restTemplate.getForObject("https://api.github.com", classOf[String])
