package order

import org.springframework.web.bind.annotation.*

import java.util

@RestController
@RequestMapping(Array("/orders"))
class OrderController(private val orderService: OrderService):

  @GetMapping
  def getAll: util.List[Order] = orderService.findAll()

  @GetMapping(Array("/{id}"))
  def getById(@PathVariable id: Long): Order =
    orderService.findById(id).getOrElse(throw new RuntimeException("Order not found"))

  @PostMapping
  def create(@RequestBody order: Order): Order =
    orderService.save(order)

  @DeleteMapping(Array("/{id}"))
  def delete(@PathVariable id: Long): Unit =
    orderService.deleteById(id)
