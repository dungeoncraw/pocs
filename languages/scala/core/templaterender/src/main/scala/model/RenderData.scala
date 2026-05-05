package com.tetokeguii.templaterender
package model

final case class RenderData(
                             value: ujson.Value
                           ) {

  def field(key: String): Option[ujson.Value] =
    value.objOpt.flatMap(_.get(key))

  def stringField(key: String): Option[String] =
    field(key).flatMap(_.strOpt)

  def arrayField(key: String): Option[List[RenderData]] =
    field(key)
      .flatMap(_.arrOpt)
      .map(_.toList.map(RenderData.apply))
}