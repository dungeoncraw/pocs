package com.tetokeguii.day22

import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*

object SimpleServer extends IOApp.Simple:

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" => Ok("world")
  }

  def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build
      .use(_ => IO.println("Server started at http://localhost:8080") *> IO.never)