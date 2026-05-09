package com.tetokeguii.templaterender
package renderers

import core.Renderer
import error.TemplateError
import error.TemplateError.RenderFailure
import model.{RenderData, TemplateDefinition}

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.ByteArrayOutputStream

object PdfRenderer extends Renderer {

  override def render(template: TemplateDefinition, data: RenderData): Either[TemplateError, Array[Byte]] = {
    HtmlRenderer.render(template, data).flatMap { htmlBytes =>
      try {
        val htmlContent = new String(htmlBytes, "UTF-8")
        val os = new ByteArrayOutputStream()
        val builder = new PdfRendererBuilder()
        builder.useFastMode()
        builder.withHtmlContent(htmlContent, "/")
        builder.toStream(os)
        builder.run()
        Right(os.toByteArray)
      } catch {
        case e: Exception => Left(RenderFailure(s"Failed to generate PDF: ${e.getMessage}"))
      }
    }
  }
}
