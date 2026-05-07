package com.tetokeguii.templaterender
package core

import error.TemplateError
import model.{RenderData, TemplateDefinition}

enum ExportFormat {
  case Html
}

class TemplateService(
    htmlRenderer: Renderer
) {

  def render(
      template: TemplateDefinition,
      data: RenderData,
      format: ExportFormat
  ): Either[TemplateError, Array[Byte]] = {
    format match {
      case ExportFormat.Html => htmlRenderer.render(template, data)
    }
  }
}
