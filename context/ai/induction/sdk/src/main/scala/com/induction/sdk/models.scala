package com.induction.sdk

import upickle.default.*

enum Action:
  case CallReal, MockWhole, Mutate, Exception, Delay

case class Transformation(
    field: String,
    value: String // Simplified for now, could be any JSON value
) derives ReadWriter

case class Profile(
    id: String,
    action: Action,
    mockResponse: Option[String] = None,
    mutations: List[Transformation] = Nil,
    exceptionMessage: Option[String] = None,
    delayMs: Option[Long] = None
) derives ReadWriter

object Action:
  implicit val rw: ReadWriter[Action] = readwriter[String].bimap(
    {
      case CallReal  => "CallReal"
      case MockWhole => "MockWhole"
      case Mutate    => "Mutate"
      case Exception => "Exception"
      case Delay     => "Delay"
    },
    {
      case "CallReal"  => CallReal
      case "MockWhole" => MockWhole
      case "Mutate"    => Mutate
      case "Exception" => Exception
      case "Delay"     => Delay
      case other       => throw new Exception(s"Unknown action: $other")
    }
  )
