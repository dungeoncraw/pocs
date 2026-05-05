package com.tetokeguii.templaterender
package model

final case class TemplateDefinition(
                                     reportName: String,
                                     elements: List[TemplateElement]
                                   )
