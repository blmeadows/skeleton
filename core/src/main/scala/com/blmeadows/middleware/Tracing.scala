package com.blmeadows.middleware

import cats.effect.{Resource, Sync}
import cats.syntax.all._
import io.chrisdavenport.fuuid.FUUID
import io.jaegertracing.Configuration.{ReporterConfiguration, SamplerConfiguration}
import natchez.jaeger.Jaeger
import natchez.{EntryPoint, Tags, Trace}

/**
 * modified from https://github.com/tpolecat/natchez-http4s
 * Start up Jaeger thus:
 *
 *  docker run -d --name jaeger \
 *    -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
 *    -p 5775:5775/udp \
 *    -p 6831:6831/udp \
 *    -p 6832:6832/udp \
 *    -p 5778:5778 \
 *    -p 16686:16686 \
 *    -p 14268:14268 \
 *    -p 9411:9411 \
 *    jaegertracing/all-in-one:1.8
 *
 * Run this example and do some requests. Go to http://localhost:16686 and select `Skeleton`
 * and search for traces.
 */
object Tracing {

  def mkRequestId[F[_]: Sync]: F[FUUID] = FUUID.randomFUUID[F]

  def trace[F[_]: Trace: Sync](id: FUUID): F[Unit] =
    for { // general things for all traces goes here
      _ <- Trace[F].put("request_id" -> id.show)
    } yield ()

  def dbTrace[F[_]: Trace: Sync](statement: String, requestId: FUUID): F[Unit] =
    for { // database specific tracing
      _ <- trace(requestId)
      _ <- Trace[F].put(Tags.db.statement(statement))
    } yield ()

  def entryPoint[F[_]: Sync]: Resource[F, EntryPoint[F]] =
    // back end options with EntryPoints available here: https://tpolecat.github.io/natchez/backends/index.html
    Jaeger.entryPoint[F](
      system = "Skeleton",
      uriPrefix = Some(new java.net.URI("http://localhost:16686"))
    ) { c =>
      Sync[F].delay {
        c.withSampler(SamplerConfiguration.fromEnv)
          .withReporter(ReporterConfiguration.fromEnv)
          .getTracer
      }
    }

}
