package product

import org.springframework.web.bind.annotation.*

import java.util

@RestController
@RequestMapping(Array("/products"))
class ProductController(private val productService: ProductService):

  @GetMapping
  def getAll: util.List[Product] = productService.findAll()

  @GetMapping(Array("/{id}"))
  def getById(@PathVariable id: Long): Product =
    productService.findById(id).getOrElse(throw new RuntimeException("Product not found"))

  @PostMapping
  def create(@RequestBody product: Product): Product =
    productService.save(product)

  @DeleteMapping(Array("/{id}"))
  def delete(@PathVariable id: Long): Unit =
    productService.deleteById(id)
