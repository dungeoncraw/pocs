package product

import jakarta.persistence.{Entity, GeneratedValue, GenerationType, Id, Table}
import scala.annotation.meta.field

@Entity
@Table(name = "products")
class Product(
               @(Id @field)
               @(GeneratedValue @field)(strategy = GenerationType.IDENTITY)
               var id: java.lang.Long = null,

               var name: String = null,

               var price: java.math.BigDecimal = null
             ):
  def this() = this(null, null, null)
