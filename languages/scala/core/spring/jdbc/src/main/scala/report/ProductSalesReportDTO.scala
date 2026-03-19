package report

import java.math.BigDecimal

case class ProductSalesReportDTO(
  productId: Long,
  productName: String,
  totalOrders: Long,
  totalAmount: Int,
  totalRevenue: BigDecimal
)
