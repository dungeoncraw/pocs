package com.tetokeguii.templaterender

import core.{ExportFormat, TemplateService}
import model.{RenderData, TemplateDefinition}
import parser.TemplateParser
import renderers.HtmlRenderer

import java.nio.file.{Files, Paths}

@main
def main(): Unit = {
  val layoutJson = ujson.Obj(
    "reportName" -> "Sales Audit Report",
    "elements" -> ujson.Arr(
      ujson.Obj(
        "type" -> "header",
        "sourceKey" -> "reportTitle"
      ),
      ujson.Obj(
        "type" -> "paragraph",
        "content" -> "This report contains details about the latest sales transactions and inventory status."
      ),
      ujson.Obj(
        "type" -> "section",
        "titleKey" -> "sectionTitle",
        "elements" -> ujson.Arr(
          ujson.Obj(
            "type" -> "table",
            "collectionKey" -> "items",
            "columns" -> ujson.Arr(
              ujson.Obj("header" -> "ID", "key" -> "id"),
              ujson.Obj("header" -> "Product", "key" -> "desc"),
              ujson.Obj("header" -> "Quantity", "key" -> "qty"),
              ujson.Obj("header" -> "Price", "key" -> "price")
            )
          )
        )
      )
    )
  )

  val templateResult = TemplateParser.parse(layoutJson)

  templateResult match {
    case Right(template) =>
      val dataJson = ujson.Obj(
        "reportTitle" -> "Q2 Sales Audit (from JSON Template)",
        "sectionTitle" -> "Transaction Details",
        "items" -> ujson.Arr(
          ujson.Obj("id" -> "S001", "desc" -> "Laptop", "qty" -> 5, "price" -> 1200.50),
          ujson.Obj("id" -> "S002", "desc" -> "Monitor", "qty" -> 10, "price" -> 300.00),
          ujson.Obj("id" -> "S003", "desc" -> "Keyboard", "qty" -> 25, "price" -> 45.99)
        )
      )
      val renderData = RenderData(dataJson)

      val templateService = new TemplateService(HtmlRenderer)

      renderAndSave(templateService, template, renderData, ExportFormat.Html, "report.html")

    case Left(error) =>
      println(s"Error parsing template: ${error.message}")
  }
}

def renderAndSave(
    service: TemplateService,
    template: TemplateDefinition,
    data: RenderData,
    format: ExportFormat,
    filename: String
): Unit = {
  println(s"Rendering $format to $filename...")
  service.render(template, data, format) match {
    case Right(bytes) =>
      val path = Paths.get(filename)
      Files.write(path, bytes)
      println(s"Successfully saved $filename")
    case Left(error) =>
      println(s"Error rendering $format: ${error.message}")
  }
}

