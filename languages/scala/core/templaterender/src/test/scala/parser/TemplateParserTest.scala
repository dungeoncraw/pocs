package com.tetokeguii.templaterender
package parser

import model.*

import org.scalatest.funsuite.AnyFunSuite

class TemplateParserTest extends AnyFunSuite {

  test("TemplateParser should parse a simple template from JSON") {
    val json = ujson.Obj(
      "reportName" -> "Test Report",
      "elements" -> ujson.Arr(
        ujson.Obj("type" -> "header", "sourceKey" -> "title"),
        ujson.Obj("type" -> "paragraph", "content" -> "Hello World")
      )
    )

    val result = TemplateParser.parse(json)
    assert(result.isRight)
    val template = result.toOption.get
    assert(template.reportName == "Test Report")
    assert(template.elements.size == 2)
    assert(template.elements.head.isInstanceOf[TemplateElement.Header])
    assert(template.elements(1).isInstanceOf[TemplateElement.Paragraph])
  }

  test("TemplateParser should parse a table from JSON") {
    val json = ujson.Obj(
      "reportName" -> "Table Report",
      "elements" -> ujson.Arr(
        ujson.Obj(
          "type" -> "table",
          "collectionKey" -> "items",
          "columns" -> ujson.Arr(
            ujson.Obj("header" -> "Name", "key" -> "name"),
            ujson.Obj("header" -> "Value", "key" -> "val")
          )
        )
      )
    )

    val result = TemplateParser.parse(json)
    assert(result.isRight)
    val template = result.toOption.get
    val table = template.elements.head.asInstanceOf[TemplateElement.Table]
    assert(table.collectionKey == "items")
    assert(table.columns.size == 2)
    assert(table.columns.head.header == "Name")
  }
}
