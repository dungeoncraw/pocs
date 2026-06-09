package db


import parser.RespValue
import parser.Command

final case class CommandResult(
                                response: RespValue,
                                persist: Option[Command] = None
                              )