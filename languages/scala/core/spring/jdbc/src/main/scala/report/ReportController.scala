package report

import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}
import scala.jdk.CollectionConverters.*

@RestController
@RequestMapping(Array("/reports"))
class ReportController(reportService: ReportService):

  @GetMapping(Array("/product-sales"))
  def getProductSalesReport: java.util.List[ProductSalesReportDTO] =
    val result = reportService.getProductSalesReport
    result.asJava
