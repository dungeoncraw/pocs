package com.tetokeguii.templaterender
package core

import error.TemplateError
import model.{RenderData, TemplateDefinition}

trait Renderer {
  def render(template: TemplateDefinition, data: RenderData): Either[TemplateError, Array[Byte]]
}
