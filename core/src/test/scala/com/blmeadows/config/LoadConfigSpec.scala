package com.blmeadows.config

import cats.effect.{Concurrent, IO}
import io.chrisdavenport.epimetheus.CollectorRegistry
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import munit.CatsEffectSuite

class LoadConfigSpec extends CatsEffectSuite {

  implicit def concurrent: Concurrent[IO] = IO.ioConcurrentEffect

  implicit def logger: Logger[IO] = Slf4jLogger.create[IO].unsafeRunSync()

  test("HttpConfig must load") {
    HttpConfigService.impl[IO].attempt.map(c => assert(c.isRight))
  }

  test("DatabaseConfig must load") {
    val cr = CollectorRegistry.defaultRegistry
    DatabaseConfigService.impl[IO](cr).attempt.map(c => assert(c.isRight))
  }

}
