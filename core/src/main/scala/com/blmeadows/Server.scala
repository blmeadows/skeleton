package com.blmeadows

import com.blmeadows.config.{DatabaseConfigService, HttpConfigService}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import cats.effect._
import cats.implicits.toSemigroupKOps
import com.blmeadows.domain.frog.{FrogRoutes, FrogService}
import com.blmeadows.domain.user.{UserRoutes, UserService}
import com.blmeadows.repository.doobie.DoobieUserRepository
import io.chrisdavenport.epimetheus.CollectorRegistry
import io.chrisdavenport.epimetheus.http4s.EpimetheusOps
import natchez.Trace
import natchez.http4s.NatchezMiddleware
import natchez.EntryPoint
import natchez.http4s.implicits.toEntryPointOps
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.client.middleware.{Metrics => ClientMetrics}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.server.middleware.{Metrics => ServerMetrics}

object Server {

  def serve[F[_]: Concurrent: ContextShift: Timer](ep: EntryPoint[F]): Resource[F, Unit] =
    for {
      httpConfigService <- Resource.eval(HttpConfigService.impl)
      app <- ep.liftR(routes)
      _ <- httpConfigService.httpServer(app.orNotFound)
    } yield ()

  def routes[F[_]: Concurrent: ContextShift: Timer: Trace]: Resource[F, HttpRoutes[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create)
      _ <- Resource.eval(logger.info("Skeleton app is starting. Have a spooky scary party!"))

      cr <- Resource.eval(CollectorRegistry.buildWithDefaults)

      client <- EmberClientBuilder.default[F].build
      clientMetricOps <- Resource.eval(EpimetheusOps.client[F](cr))
      clientMetered = ClientMetrics[F](clientMetricOps, _.uri.host.map(_.value))(client)
      frogService = FrogService(clientMetered)
      frogRoutes = FrogRoutes.routes(frogService)

      dbConfigService <- Resource.eval(DatabaseConfigService.impl(cr))
      _ <- Resource.eval(dbConfigService.runMigrations)
      transactor <- dbConfigService.transactor
      userService = UserService(DoobieUserRepository.impl(transactor))
      userRoutes = UserRoutes.routes(logger, userService)

      routes = userRoutes <+> frogRoutes

      serverMetricOps <- Resource.eval(EpimetheusOps.server(cr))
      meteredRoutes = ServerMetrics(serverMetricOps)(routes)
    } yield NatchezMiddleware.server(meteredRoutes)

// from Chris in #natchez in discord, consider later
//
//  def spannedFK[F[_]: Trace]: Instrumentation[F, *] ~> F =
//    new ~>[Instrumentation[F, *], F] {
//      def apply[A](fa: Instrumentation[F, A]): F[A] =
//        Trace[F].span(s"${fa.algebraName}.${fa.methodName}")(fa.value)
//    }
//
//  /** Given a span, we lift an instrumented algebra to a Kleisli so we can trace, then lower back to the original effect. */
//  def withTracing[Alg[_[_]]: Instrument, F[_]: Trace](alg: Alg[F]): Alg[F] =
//    Instrument[Alg].instrument(alg).mapK(spannedFK[F])
//
// Chris says:
// Haven't adopted it to the most recent changes though
// Basically its free tracing in your code along your covariant F algebras
// But if you're paying by span(very frequent approach) you may want to be more discerning.

}
