package com.blmeadows.domain.frog

import cats.effect.Sync
import cats.syntax.all._
import com.blmeadows.middleware.Tracing.{mkRequestId, trace}
import io.circe.syntax._
import natchez.Trace
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object FrogRoutes {

  def routes[F[_]: Sync: Trace](frogService: FrogService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "frog" =>
        for {
          requestId <- mkRequestId
          _ <- trace[F](requestId)
          frog <- frogService.getFrog("", requestId)
          res <- Ok(frog.asJson)
        } yield res

    }
  }
}
