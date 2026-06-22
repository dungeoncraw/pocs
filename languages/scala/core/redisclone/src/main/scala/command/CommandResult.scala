package command

import parser.RespValue
import parser.RespValue

final case class CommandResult(
                                response: RespValue,
                                persist: Option[Command] = None
                              )