package report

import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import java.util.Collections
import java.math.BigDecimal

class ReportServiceTest extends AnyWordSpec with MockitoSugar:

  val reportDAO: ReportDAO = mock[ReportDAO]
  val reportService = new ReportService(reportDAO)

  "ReportService" should {
    "generate product sales report" in {
      val rawData: java.util.List[Array[AnyRef]] = new java.util.ArrayList()
      rawData.add(Array(1L.asInstanceOf[AnyRef], "Product A", 10L.asInstanceOf[AnyRef], 100L.asInstanceOf[AnyRef], new BigDecimal("1000.00")))
      
      when(reportDAO.findProductSales()).thenReturn(rawData)
      
      val result = reportService.getProductSalesReport
      
      assert(result.size == 1)
      val report = result.head
      assert(report.productId == 1L)
      assert(report.productName == "Product A")
      assert(report.totalOrders == 10L)
      assert(report.totalAmount == 100)
      assert(report.totalRevenue == new BigDecimal("1000.00"))
      
      verify(reportDAO).findProductSales()
    }
    
    "return empty list when no data is present" in {
      when(reportDAO.findProductSales()).thenReturn(new java.util.ArrayList())
      
      val result = reportService.getProductSalesReport
      
      assert(result.isEmpty)
    }
  }
