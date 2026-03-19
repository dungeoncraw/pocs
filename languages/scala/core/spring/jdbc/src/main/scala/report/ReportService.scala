package report

import org.springframework.stereotype.Service
import scala.jdk.CollectionConverters.*
import java.math.BigDecimal

@Service
class ReportService(reportDAO: ReportDAO):

  def getProductSalesReport: List[ProductSalesReportDTO] =
    val rawData = reportDAO.findProductSales().asScala.toList
    rawData.map { row =>
      ProductSalesReportDTO(
        productId = row(0).asInstanceOf[Long],
        productName = row(1).asInstanceOf[String],
        totalOrders = row(2).asInstanceOf[Long],
        totalAmount = row(3).asInstanceOf[java.lang.Long].toInt,
        totalRevenue = row(4).asInstanceOf[BigDecimal]
      )
    }
