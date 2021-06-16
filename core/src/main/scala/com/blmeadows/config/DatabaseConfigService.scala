package com.blmeadows.config

import cats.effect._
import cats.implicits._
import com.blmeadows.config.Configurations.PostgresConfig
import com.blmeadows.repository.DatabaseMigrations
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.chrisdavenport.epimetheus.CollectorRegistry
import io.chrisdavenport.log4cats.Logger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

trait DatabaseConfigService[F[_]] {
  def transactor: Resource[F, Transactor[F]]
  def runMigrations: F[Int]
}

object DatabaseConfigService {

  def impl[F[_]: Concurrent: ContextShift: Logger](
      cr: CollectorRegistry[F]
  ): F[DatabaseConfigService[F]] =
    for {
      dbConfig <- Sync[F].delay(
        ConfigSource.default.at("postgres").loadOrThrow[PostgresConfig]
      )
    } yield new DatabaseConfigService[F] {

      override def transactor: Resource[F, Transactor[F]] =
        for {
          connectEC <- ExecutionContexts.fixedThreadPool[F](dbConfig.threadPoolSize)
          blocker <- Blocker[F]
          // https://github.com/tpolecat/doobie/blob/master/modules/docs/src/main/mdoc/docs/14-Managing-Connections.md#using-a-hikaricp-connection-pool
          transactor <- HikariTransactor.newHikariTransactor[F](
            dbConfig.driver,
            dbConfig.jdbcUrl,
            dbConfig.username,
            dbConfig.password,
            connectEC,
            blocker
          )
          _ = Resource.eval(
            Sync[F].delay(
              transactor.kernel.setMetricsTrackerFactory(
                new PrometheusMetricsTrackerFactory(
                  CollectorRegistry.Unsafe.asJava(cr) // epimetheus to prometheus
                )
              )
            )
          )
        } yield transactor

      override def runMigrations: F[Int] =
        DatabaseMigrations.migrate[F](
          dbConfig.jdbcUrl,
          dbConfig.username,
          dbConfig.password
        )
    }

}
