package com.tetokeguii.templaterender
package core

import error.TemplateError
import model.{RenderData, TemplateDefinition}

enum ExportFormat {
  case Html
  case Pdf
  case Csv
}

class TemplateService(
    htmlRenderer: Renderer,
    pdfRenderer: Renderer,
    csvRenderer: Renderer
) {

  def render(
      template: TemplateDefinition,
      data: RenderData,
      format: ExportFormat
  ): Either[TemplateError, Array[Byte]] = {
    format match {
      case ExportFormat.Html => htmlRenderer.render(template, data)
      case ExportFormat.Pdf  => pdfRenderer.render(template, data)
      case ExportFormat.Csv  => csvRenderer.render(template, data)
    }
  }
}
