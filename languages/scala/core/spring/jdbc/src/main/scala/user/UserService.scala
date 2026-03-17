package user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scala.jdk.CollectionConverters.*
import java.util.Optional

@Service
class UserService(private val userRepository: UserRepository):

  @Transactional(readOnly = true)
  def findAll(): java.util.List[User] =
    userRepository.findAll().asScala.toList.asJava

  @Transactional(readOnly = true)
  def findById(id: Long): Optional[User] =
    userRepository.findById(id)

  @Transactional
  def save(user: User): User =
    userRepository.save(user)

  @Transactional
  def deleteById(id: Long): Unit =
    userRepository.deleteById(id)
