package order

import jakarta.persistence.*
import scala.annotation.meta.field
import product.Product

@Entity
@Table(name = "orders")
class Order(
             @(Id @field)
             @(GeneratedValue @field)(strategy = GenerationType.IDENTITY)
             var id: java.lang.Long = null,

             @Column(name = "user_id")
             var userId: java.lang.Long = null,

             @(ManyToOne @field)(fetch = FetchType.LAZY)
             @(JoinColumn @field)(name = "product_id")
             var product: Product = null,

             var amount: java.lang.Integer = null
           ):
  def this() = this(null, null, null, null)
