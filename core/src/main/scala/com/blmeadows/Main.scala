package com.blmeadows

import cats.effect.{ExitCode, IO, IOApp}
import com.blmeadows.middleware.Tracing.entryPoint

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    entryPoint[IO].flatMap { ep =>
      Server.serve[IO](ep)
    }
  }.use(_ => IO.never).as(ExitCode.Success)
}
