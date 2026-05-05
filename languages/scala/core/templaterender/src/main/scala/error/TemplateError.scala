package com.tetokeguii.templaterender
package error

sealed trait TemplateError {
  def message: String
}

object TemplateError {

  final case class InvalidTemplate(
                                    message: String
                                  ) extends TemplateError

  final case class InvalidData(
                                message: String
                              ) extends TemplateError

  final case class MissingField(
                                 field: String
                               ) extends TemplateError {
    override val message: String = s"Missing required field: $field"
  }

  final case class UnsupportedElementType(
                                           elementType: String
                                         ) extends TemplateError {
    override val message: String = s"Unsupported template element type: $elementType"
  }

  final case class UnsupportedFormat(
                                      format: String
                                    ) extends TemplateError {
    override val message: String = s"Unsupported output format: $format"
  }

  final case class RenderFailure(
                                  message: String
                                ) extends TemplateError

  final case class ParserFailure(
                                  message: String
                                ) extends TemplateError
}