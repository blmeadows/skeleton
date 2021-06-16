package com.blmeadows.repository

import cats.effect.Sync
import cats.syntax.all._
import io.chrisdavenport.log4cats.Logger
import org.flywaydb.core.Flyway

object DatabaseMigrations {
  def migrate[F[_]: Sync: Logger](url: String, user: String, password: String): F[Int] =
    Sync[F]
      .delay {
        Flyway
          .configure()
          .dataSource(url, user, password)
          .load()
          .migrate()
      }
      .attempt
      .flatMap {
        case Right(result) =>
          Logger[F].info(s"completed ${result.migrationsExecuted} migrations") >>
            Sync[F].delay(result.migrationsExecuted)
        case Left(fail) =>
          Logger[F].error(fail)(s"migrations failed: ${fail.getMessage}") >>
            Sync[F].raiseError(fail)
      }
}
