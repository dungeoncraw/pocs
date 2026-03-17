package order

import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import java.util.Optional
import java.util.Collections
import product.Product

class OrderServiceTest extends AnyWordSpec with MockitoSugar:

  val orderRepository: OrderRepository = mock[OrderRepository]
  val orderService = new OrderService(orderRepository)

  "OrderService" should {
    "find all orders" in {
      when(orderRepository.findAll()).thenReturn(Collections.emptyList[Order]())
      
      val result = orderService.findAll()
      
      assert(result.isEmpty)
      verify(orderRepository).findAll()
    }

    "find order by id" in {
      val order = new Order(1L, 1L, new Product(), 5)
      when(orderRepository.findById(1L)).thenReturn(Optional.of(order))
      
      val result = orderService.findById(1L)
      
      assert(result.isDefined)
      assert(result.get.amount == 5)
      verify(orderRepository).findById(1L)
    }

    "save an order" in {
      val order = new Order(null, 1L, new Product(), 10)
      val savedOrder = new Order(1L, 1L, new Product(), 10)
      when(orderRepository.save(order)).thenReturn(savedOrder)
      
      val result = orderService.save(order)
      
      assert(result.id == 1L)
      verify(orderRepository).save(order)
    }
  }
