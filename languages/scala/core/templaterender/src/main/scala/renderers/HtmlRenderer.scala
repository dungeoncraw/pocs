package com.tetokeguii.templaterender
package renderers

import core.Renderer
import error.TemplateError
import error.TemplateError.MissingField
import model.{RenderData, TemplateDefinition, TemplateElement}

import scalatags.Text.TypedTag
import scalatags.Text.all.*

object HtmlRenderer extends Renderer {

  override def render(template: TemplateDefinition, data: RenderData): Either[TemplateError, Array[Byte]] = {
    renderToHtml(template, data).map(_.render.getBytes("UTF-8"))
  }

  private def renderToHtml(template: TemplateDefinition, data: RenderData): Either[TemplateError, TypedTag[String]] = {
    val renderedElements = template.elements.map(el => renderElement(el, data))
    
    val errors = renderedElements.collect { case Left(e) => e }
    if (errors.nonEmpty) {
      Left(errors.head)
    } else {
      val bodyContent = renderedElements.collect { case Right(tags) => tags }
      Right(
        html(
          head(
            tag("style")(
              """
                body { font-family: sans-serif; margin: 40px; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
                h1 { color: #333; }
              """
            )
          ),
          body(
            h1(template.reportName),
            bodyContent
          )
        )
      )
    }
  }

  private def renderElement(element: TemplateElement, data: RenderData): Either[TemplateError, TypedTag[String]] = {
    element match {
      case TemplateElement.Header(sourceKey) =>
        data.stringField(sourceKey) match {
          case Some(text) => Right(h2(text))
          case None => Left(MissingField(sourceKey))
        }

      case TemplateElement.Paragraph(content) =>
        Right(p(content))

      case TemplateElement.Table(collectionKey, columns) =>
        data.arrayField(collectionKey) match {
          case Some(rows) =>
            val headerRow = tr(columns.map(col => th(col.header)))
            val dataRows = rows.map { rowData =>
              tr(
                columns.map { col =>
                  val cellValue = rowData.field(col.key).map(_.toString).getOrElse("")
                  td(cellValue)
                }
              )
            }
            Right(table(thead(headerRow), tbody(dataRows)))
          case None => Left(MissingField(collectionKey))
        }

      case TemplateElement.Section(titleKey, elements) =>
        val titleTag = titleKey.flatMap(data.stringField).map(h3(_))
        val renderedSubElements = elements.map(el => renderElement(el, data))
        
        val errors = renderedSubElements.collect { case Left(e) => e }
        if (errors.nonEmpty) {
          Left(errors.head)
        } else {
          val content = renderedSubElements.collect { case Right(tags) => tags }
          Right(div(titleTag.getOrElse(span()), content))
        }
    }
  }
}
