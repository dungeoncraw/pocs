package db

import parser.{Command, RespBulkString, RespSimpleString, RespValues}

object PingCommand:

  def execute(args: List[String]): CommandResult =
    args match
      case Nil =>
        CommandResult(
          response = RespSimpleString("PONG")
        )

      case message :: Nil =>
        CommandResult(
          response = RespValues.bulkString(message)
        )

      case _ =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("ping")
        )


object SetCommand:

  def execute(
               database: RedisDatabase,
               command: Command
             ): CommandResult =
    command.args match
      case key :: value :: Nil =>
        database.setString(
          key = key,
          value = CommandSupport.bytes(value)
        )

        CommandResult(
          response = RespSimpleString("OK"),
          persist = Some(command)
        )

      case _ =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("set")
        )


object GetCommand:

  def execute(
               database: RedisDatabase,
               args: List[String]
             ): CommandResult =
    args match
      case key :: Nil =>
        database.getString(key) match
          case Right(Some(value)) =>
            CommandResult(
              response = RespBulkString(Some(value))
            )

          case Right(None) =>
            CommandResult(
              response = RespBulkString(None)
            )

          case Left(_) =>
            CommandResult(
              response = CommandSupport.wrongType
            )

      case _ =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("get")
        )