package com.tetokeguii.templaterender
package renderers

import core.Renderer
import error.TemplateError
import error.TemplateError.MissingField
import model.{RenderData, TemplateDefinition, TemplateElement}

import kantan.csv.*
import kantan.csv.ops.*
import java.io.ByteArrayOutputStream

object CsvRenderer extends Renderer {

  override def render(template: TemplateDefinition, data: RenderData): Either[TemplateError, Array[Byte]] = {
    findTable(template.elements) match {
      case Some(table) => renderTable(table, data)
      case None => Left(TemplateError.UnsupportedElementType("No table found for CSV rendering"))
    }
  }

  private def findTable(elements: List[TemplateElement]): Option[TemplateElement.Table] = {
    elements.view.flatMap {
      case t: TemplateElement.Table => Some(t)
      case s: TemplateElement.Section => findTable(s.elements)
      case _ => None
    }.headOption
  }

  private def renderTable(table: TemplateElement.Table, data: RenderData): Either[TemplateError, Array[Byte]] = {
    data.arrayField(table.collectionKey) match {
      case Some(rows) =>
        val headers = table.columns.map(_.header)
        val dataRows = rows.map { rowData =>
          table.columns.map { col =>
            rowData.field(col.key).map {
              case ujson.Str(s) => s
              case ujson.Num(n) => if (n == n.toInt) n.toInt.toString else n.toString
              case ujson.Bool(b) => b.toString
              case ujson.Null => ""
              case other => other.toString
            }.getOrElse("")
          }
        }

        val out = new ByteArrayOutputStream()
        val writer = out.asCsvWriter[List[String]](rfc.withHeader(headers*))
        
        dataRows.foreach(writer.write)
        writer.close()
        
        Right(out.toByteArray)

      case None => Left(MissingField(table.collectionKey))
    }
  }
}
