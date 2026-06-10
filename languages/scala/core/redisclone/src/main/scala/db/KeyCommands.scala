package db

import parser.{Command, RespInteger}

object ExpireCommand:

  def execute(
               database: RedisDatabase,
               args: List[String]
             ): CommandResult =
    args match
      case key :: secondsText :: Nil =>
        CommandSupport.parseLong(secondsText) match
          case Left(error) =>
            CommandResult(error)

          case Right(seconds) =>
            val now =
              System.currentTimeMillis()

            val expiresAtResult =
              for
                millis <- CommandSupport.multiplyExact(seconds, 1000L)
                expiresAt <- CommandSupport.addExact(now, millis)
              yield expiresAt

            expiresAtResult match
              case Left(error) =>
                CommandResult(error)

              case Right(expiresAtMillis) =>
                val changed =
                  database.expireAt(key, expiresAtMillis)

                CommandResult(
                  response =
                    RespInteger(if changed then 1L else 0L),
                  persist =
                    if changed then
                      Some(
                        Command(
                          name = "PEXPIREAT",
                          args = List(key, expiresAtMillis.toString)
                        )
                      )
                    else
                      None
                )

      case _ =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("expire")
        )


object PExpireAtCommand:

  def execute(
               database: RedisDatabase,
               command: Command
             ): CommandResult =
    command.args match
      case key :: timestampMillisText :: Nil =>
        CommandSupport.parseLong(timestampMillisText) match
          case Left(error) =>
            CommandResult(error)

          case Right(timestampMillis) =>
            val changed =
              database.expireAt(key, timestampMillis)

            CommandResult(
              response =
                RespInteger(if changed then 1L else 0L),
              persist =
                if changed then Some(command)
                else None
            )

      case _ =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("pexpireat")
        )


object TtlCommand:

  def execute(
               database: RedisDatabase,
               args: List[String]
             ): CommandResult =
    args match
      case key :: Nil =>
        CommandResult(
          response = RespInteger(database.ttlSeconds(key))
        )


object DelCommand:

  def execute(
               database: RedisDatabase,
               args: List[String]
             ): CommandResult =
    args match
      case Nil =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("del")
        )

      case keys =>
        CommandResult(
          response = RespInteger(database.deleteMany(keys))
        )


object ExistsCommand:

  def execute(
               database: RedisDatabase,
               args: List[String]
             ): CommandResult =
    args match
      case Nil =>
        CommandResult(
          response = CommandSupport.wrongNumberOfArguments("exists")
        )

      case keys =>
        CommandResult(
          response = RespInteger(database.existsMany(keys))
        )