package user

import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import java.util.Optional
import java.util.Collections

class UserServiceTest extends AnyWordSpec with MockitoSugar:

  val userRepository: UserRepository = mock[UserRepository]
  val userService = new UserService(userRepository)

  "UserService" should {
    "find all users" in {
      val users = Collections.emptyList[User]()
      when(userRepository.findAll()).thenReturn(Collections.emptyList[User]())
      
      val result = userService.findAll()
      
      assert(result.isEmpty)
      verify(userRepository).findAll()
    }

    "find user by id" in {
      val user = User(1L, "John", "john@example.com")
      when(userRepository.findById(1L)).thenReturn(Optional.of(user))
      
      val result = userService.findById(1L)
      
      assert(result.isPresent)
      assert(result.get().name == "John")
      verify(userRepository).findById(1L)
    }

    "save a user" in {
      val user = User(null, "John", "john@example.com")
      val savedUser = User(1L, "John", "john@example.com")
      when(userRepository.save(user)).thenReturn(savedUser)
      
      val result = userService.save(user)
      
      assert(result.id == 1L)
      verify(userRepository).save(user)
    }
  }
