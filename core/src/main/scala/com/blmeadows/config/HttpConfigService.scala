package com.blmeadows.config

import cats.effect._
import cats.implicits._
import com.blmeadows.config.Configurations.HttpConfig
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import pureconfig.ConfigSource
import pureconfig.generic.auto._

trait HttpConfigService[F[_]] {
  def httpServer(app: HttpApp[F]): Resource[F, Server[F]]
}

object HttpConfigService {

  def impl[F[_]: Concurrent: Timer: ContextShift]: F[HttpConfigService[F]] =
    for {
      httpConfig <- Sync[F].delay(
        ConfigSource.default.at("http").loadOrThrow[HttpConfig]
      )
    } yield new HttpConfigService[F] {

      override def httpServer(app: HttpApp[F]): Resource[F, Server[F]] =
        EmberServerBuilder
          .default[F]
          .withHost(httpConfig.host)
          .withPort(httpConfig.port)
          .withHttpApp(app)
          .build
    }

}
