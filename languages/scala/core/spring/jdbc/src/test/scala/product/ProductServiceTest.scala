package product

import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import java.util.Optional
import java.util.Collections
import java.math.BigDecimal

class ProductServiceTest extends AnyWordSpec with MockitoSugar:

  val productRepository: ProductRepository = mock[ProductRepository]
  val productService = new ProductService(productRepository)

  "ProductService" should {
    "find all products" in {
      when(productRepository.findAll()).thenReturn(Collections.emptyList[Product]())
      
      val result = productService.findAll()
      
      assert(result.isEmpty)
      verify(productRepository).findAll()
    }

    "find product by id" in {
      val product = new Product(1L, "Laptop", new BigDecimal("999.99"))
      when(productRepository.findById(1L)).thenReturn(Optional.of(product))
      
      val result = productService.findById(1L)
      
      assert(result.isDefined)
      assert(result.get.name == "Laptop")
      verify(productRepository).findById(1L)
    }

    "save a product" in {
      val product = new Product(null, "Laptop", new BigDecimal("999.99"))
      val savedProduct = new Product(1L, "Laptop", new BigDecimal("999.99"))
      when(productRepository.save(product)).thenReturn(savedProduct)
      
      val result = productService.save(product)
      
      assert(result.id == 1L)
      verify(productRepository).save(product)
    }
  }
