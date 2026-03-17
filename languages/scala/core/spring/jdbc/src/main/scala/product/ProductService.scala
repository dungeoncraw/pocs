package product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util

@Service
class ProductService(private val productRepository: ProductRepository):

  @Transactional(readOnly = true)
  def findAll(): util.List[Product] = productRepository.findAll()

  @Transactional(readOnly = true)
  def findById(id: Long): Option[Product] = 
    Option(productRepository.findById(id).orElse(null))

  @Transactional
  def save(product: Product): Product = productRepository.save(product)

  @Transactional
  def deleteById(id: Long): Unit = productRepository.deleteById(id)
