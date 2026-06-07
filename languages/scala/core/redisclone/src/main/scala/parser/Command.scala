package parser

import java.nio.charset.StandardCharsets

final case class Command(
                          name: String,
                          args: List[String]
                        )

object Command:

  def fromResp(value: RespValue): Either[String, Command] =
    value match
      case RespArray(Some(values)) =>
        parseCommandArray(values)

      case RespArray(None) =>
        Left("ERR null array is not a valid command")

      case _ =>
        Left("ERR expected array command")

  private def parseCommandArray(values: List[RespValue]): Either[String, Command] =
    val strings = List.newBuilder[String]

    var error: Option[String] = None
    val it = values.iterator
    while it.hasNext && error.isEmpty do
      it.next() match
        case RespBulkString(Some(bytes)) =>
          strings += String(bytes, StandardCharsets.UTF_8)

        case RespBulkString(None) =>
          error = Some("ERR null bulk string is not valid inside command")

        case _ =>
          error = Some("ERR command must be an array of bulk strings")

    error match
      case Some(err) => Left(err)
      case None =>
        val parts = strings.result()

        parts match
          case Nil =>
            Left("ERR empty command")

          case commandName :: args =>
            Right(
              Command(
                name = commandName.toUpperCase,
                args = args
              )
            )