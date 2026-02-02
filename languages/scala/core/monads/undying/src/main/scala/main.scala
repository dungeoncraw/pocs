package com.dungeoncraw.undying

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

object Undying:

  final case class GraveId(value: String)
  private final case class Grave(id: GraveId, soilQuality: Int, occupant: String, hasWardingSigil: Boolean)
  final case class Zombie(name: String, threat: Int, origin: GraveId)

  enum RitualError:
    case Missing(field: String)
    case BadFormat(field: String, details: String)
    case Forbidden(details: String)
    case GraveNotFound(id: GraveId)
    case WardNetworkFailed(details: String)

  private def require[A](opt: Option[A], field: String): Either[RitualError, A] =
    opt.toRight(RitualError.Missing(field))

  private def parseToInt(s: String, field: String): Either[RitualError, Int] =
    Try(s.toInt).toEither.left.map(_ => RitualError.BadFormat(field, s"not an int: '$s'"))

  private val graveyard: Map[GraveId, Grave] =
    Map(
      GraveId("G-101") -> Grave(GraveId("G-101"), soilQuality = 7, occupant = "Sir Ian Doyle", hasWardingSigil = false),
      GraveId("G-102") -> Grave(GraveId("G-102"), soilQuality = 3, occupant = "Ana Ban", hasWardingSigil = false),
      GraveId("G-103") -> Grave(GraveId("G-103"), soilQuality = 9, occupant = "Alice Chains", hasWardingSigil = false),
      GraveId("G-104") -> Grave(GraveId("G-104"), soilQuality = 5, occupant = "Don Presto Bar Ba", hasWardingSigil = true),
      GraveId("G-105") -> Grave(GraveId("G-105"), soilQuality = 12, occupant = "John John Jr.", hasWardingSigil = false),
      GraveId("G-106") -> Grave(GraveId("G-106"), soilQuality = 4, occupant = "Fabian Jr.", hasWardingSigil = false),
      GraveId("G-107") -> Grave(GraveId("G-107"), soilQuality = 15, occupant = "Dustin", hasWardingSigil = true),
      GraveId("G-108") -> Grave(GraveId("G-108"), soilQuality = 6, occupant = "Silas Works", hasWardingSigil = false),
      GraveId("G-109") -> Grave(GraveId("G-109"), soilQuality = 8, occupant = "Raven unit", hasWardingSigil = false),
      GraveId("G-110") -> Grave(GraveId("G-110"), soilQuality = 2, occupant = "Timmy Grapper", hasWardingSigil = false)
    )

  private def getGrave(id: GraveId): Either[RitualError, Grave] =
    graveyard.get(id).toRight(RitualError.GraveNotFound(id))

  private def generateCandidates(grave: Grave, intensity: Int, moonPhase: Option[String]): List[Zombie] =
    val baseThreat = (grave.soilQuality + intensity).min(20)
    val moonBonus  = moonPhase.map(_.toLowerCase) match
      case Some("blood") => 5
      case Some("full")  => 2
      case _             => 0

    List(
      s"${grave.occupant}, the Shambler",
      s"Rotting ${grave.occupant}",
      s"${grave.occupant}'s Undoing"
    ).map(n => Zombie(n, threat = baseThreat + moonBonus, origin = grave.id))

  private def wardCheck(z: Zombie, grave: Grave)(using ExecutionContext): Future[Either[RitualError, Zombie]] =
    Future.successful {
      if grave.hasWardingSigil && z.threat < 15 then Left(RitualError.Forbidden(s"warding sigil blocked grave ${grave.id.value}"))
      else if z.threat > 22 then Left(RitualError.Forbidden(s"zombie too strong: threat=${z.threat}"))
      else Right(z)
    }.recover { case ex =>
      Left(RitualError.WardNetworkFailed(ex.getClass.getSimpleName))
    }

  private def traverseFailFast[A, B](
                              xs: List[A]
                            )(f: A => Future[Either[RitualError, B]]
                            )(using ExecutionContext
                            ): Future[Either[RitualError, List[B]]] =
    xs.foldLeft(Future.successful(Right(Nil): Either[RitualError, List[B]])) { (accF, a) =>
      accF.flatMap {
        case Left(err) => Future.successful(Left(err))
        case Right(acc) =>
          f(a).map {
            case Left(err) => Left(err)
            case Right(b)  => Right(acc :+ b)
          }
      }
    }

  private def traverseAccumulating[A, B](
                                  xs: List[A]
                                )(f: A => Future[Either[RitualError, B]]
                                )(using ExecutionContext
                                ): Future[Either[List[RitualError], List[B]]] =
    xs.foldLeft(Future.successful(Right(Nil): Either[List[RitualError], List[B]])) { (accF, a) =>
      accF.flatMap { acc =>
        f(a).map {
          case Left(err) =>
            acc match
              case Left(errs) => Left(errs :+ err)
              case Right(_)   => Left(List(err))
          case Right(b) =>
            acc match
              case Left(errs) => Left(errs)
              case Right(bs)  => Right(bs :+ b)
        }
      }
    }

  def performRitual(scroll: Map[String, String])(using ExecutionContext): Future[Either[RitualError, List[Zombie]]] =
    val validated: Either[RitualError, (List[GraveId], Int, Option[String])] =
      for
        graveIdsRaw  <- require(scroll.get("graves"), "graves")
        intensityRaw <- require(scroll.get("intensity"), "intensity")
        intensity    <- parseToInt(intensityRaw, "intensity")
        _            <- if intensity >= 1 && intensity <= 10 then Right(())
        else Left(RitualError.Forbidden("intensity must be 1..10"))
        moonPhase     = scroll.get("moon").map(_.trim).filter(_.nonEmpty)
        ids           = graveIdsRaw.split(",").toList.map(_.trim).filter(_.nonEmpty).map(GraveId(_))
        _            <- if ids.size >= 3 then Right(())
        else Left(RitualError.Forbidden("must provide at least 3 graves"))
      yield (ids, intensity, moonPhase)

    validated match
      case Left(err) =>
        Future.successful(Left(err))

      case Right((ids, intensity, moonPhase)) =>
        val pairsE: Either[RitualError, List[(Zombie, Grave)]] =
          ids.foldLeft[Either[RitualError, List[(Zombie, Grave)]]](Right(Nil)) { (accE, id) =>
            for
              acc   <- accE
              grave <- getGrave(id)
              zs     = generateCandidates(grave, intensity, moonPhase)
            yield acc ++ zs.map(z => (z, grave))
          }

        pairsE match
          case Left(err) =>
            Future.successful(Left(err))

          case Right(pairs) =>
            traverseFailFast(pairs) { case (z, grave) => wardCheck(z, grave) }
              .map(_.map(raised => raised.distinct))

  def performRitualAccumulating(scroll: Map[String, String])(using ExecutionContext): Future[Either[List[RitualError], List[Zombie]]] =
    val validated: Either[List[RitualError], (List[GraveId], Int, Option[String])] = {
      val errors = List.newBuilder[RitualError]
      val graveIdsRaw = scroll.getOrElse("graves", {
        errors += RitualError.Missing("graves")
        ""
      })
      val intensityRaw = scroll.getOrElse("intensity", {
        errors += RitualError.Missing("intensity")
        ""
      })

      val intensityOpt = if intensityRaw.nonEmpty then
        parseToInt(intensityRaw, "intensity") match
          case Left(err) =>
            errors += err
            None
          case Right(i) =>
            if i < 1 || i > 10 then
              errors += RitualError.Forbidden("intensity must be 1..10")
              None
            else Some(i)
      else None

      val moonPhase = scroll.get("moon").map(_.trim).filter(_.nonEmpty)
      val ids = graveIdsRaw.split(",").toList.map(_.trim).filter(_.nonEmpty).map(GraveId(_))

      if ids.nonEmpty && ids.size < 3 then
        errors += RitualError.Forbidden("must provide at least 3 graves")

      val allErrors = errors.result()
      if allErrors.nonEmpty then Left(allErrors)
      else (intensityOpt match {
        case Some(i) => Right((ids, i, moonPhase))
        case None => Left(List(RitualError.Missing("intensity")))
      })
    }

    validated match
      case Left(errs) =>
        Future.successful(Left(errs))

      case Right((ids, intensity, moonPhase)) =>
        val pairsE: Either[List[RitualError], List[(Zombie, Grave)]] = {
          val errors = List.newBuilder[RitualError]
          val results = List.newBuilder[(Zombie, Grave)]
          ids.foreach { id =>
            getGrave(id) match
              case Left(err) => errors += err
              case Right(grave) =>
                val zs = generateCandidates(grave, intensity, moonPhase)
                results ++= zs.map(z => (z, grave))
          }
          val allErrors = errors.result()
          if allErrors.nonEmpty then Left(allErrors)
          else Right(results.result())
        }

        pairsE match
          case Left(errs) =>
            Future.successful(Left(errs))

          case Right(pairs) =>
            traverseAccumulating(pairs) { case (z, grave) => wardCheck(z, grave) }
              .map(_.map(raised => raised.distinct))

@main def main(): Unit =
  given ExecutionContext = ExecutionContext.global

  val scroll: Map[String, String] =
    Map("graves" -> "G-101,G-102,G-103", "intensity" -> "6", "moon" -> "blood")

  val result = Await.result(Undying.performRitual(scroll), 5.seconds)
  println("\nFail fast\n")
  println(result)

  val resultAcc = Await.result(Undying.performRitualAccumulating(scroll), 5.seconds)
  println("\nAccumulating errors\n")
  println(resultAcc)