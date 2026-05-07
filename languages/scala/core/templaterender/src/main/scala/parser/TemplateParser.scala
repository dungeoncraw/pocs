package com.tetokeguii.templaterender
package parser

import error.TemplateError
import error.TemplateError.{ParserFailure, UnsupportedElementType}
import model.{ColumnDefinition, TemplateDefinition, TemplateElement}

object TemplateParser {

  def parse(json: ujson.Value): Either[TemplateError, TemplateDefinition] = {
    try {
      val reportName = json("reportName").str
      val elementsJson = json("elements").arr
      
      val elements = elementsJson.map(parseElement).toList
      
      val errors = elements.collect { case Left(e) => e }
      if (errors.nonEmpty) {
        Left(errors.head)
      } else {
        Right(TemplateDefinition(reportName, elements.collect { case Right(el) => el }))
      }
    } catch {
      case e: Exception => Left(ParserFailure(s"Failed to parse template: ${e.getMessage}"))
    }
  }

  private def parseElement(json: ujson.Value): Either[TemplateError, TemplateElement] = {
    try {
      val elementType = json("type").str
      elementType match {
        case "header" =>
          Right(TemplateElement.Header(json("sourceKey").str))
        
        case "paragraph" =>
          Right(TemplateElement.Paragraph(json("content").str))
          
        case "table" =>
          val collectionKey = json("collectionKey").str
          val columns = json("columns").arr.map { colJson =>
            ColumnDefinition(colJson("header").str, colJson("key").str)
          }.toList
          Right(TemplateElement.Table(collectionKey, columns))
          
        case "section" =>
          val titleKey = if (json.obj.contains("titleKey")) Some(json("titleKey").str) else None
          val subElementsJson = json("elements").arr
          val subElements = subElementsJson.map(parseElement).toList
          
          val errors = subElements.collect { case Left(e) => e }
          if (errors.nonEmpty) {
            Left(errors.head)
          } else {
            Right(TemplateElement.Section(titleKey, subElements.collect { case Right(el) => el }))
          }
          
        case other => Left(UnsupportedElementType(other))
      }
    } catch {
      case e: Exception => Left(ParserFailure(s"Failed to parse element: ${e.getMessage}"))
    }
  }
}
