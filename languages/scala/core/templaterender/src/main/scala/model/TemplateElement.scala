package com.tetokeguii.templaterender
package model

sealed trait TemplateElement

object TemplateElement {
  final case class Header(
                         sourceKey: String,
                         ) extends TemplateElement

  final case class Paragraph(
                         content: String,
                         ) extends TemplateElement
  final case class Table(
                         collectionKey: String,
                         columns: List[ColumnDefinition]
                         ) extends TemplateElement
  final case class Section(
                          titleKey: Option[String],
                          elements: List[TemplateElement]
                          ) extends TemplateElement
}
