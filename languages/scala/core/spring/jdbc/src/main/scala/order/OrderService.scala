package order

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.List

@Service
class OrderService(private val orderRepository: OrderRepository):

  @Transactional(readOnly = true)
  def findAll(): util.List[Order] = orderRepository.findAll()

  @Transactional(readOnly = true)
  def findById(id: Long): Option[Order] =
    Option(orderRepository.findById(id).orElse(null))

  @Transactional
  def save(order: Order): Order = orderRepository.save(order)

  @Transactional
  def deleteById(id: Long): Unit = orderRepository.deleteById(id)
