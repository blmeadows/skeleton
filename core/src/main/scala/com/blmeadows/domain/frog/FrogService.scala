package com.blmeadows.domain.frog

import cats.effect.Sync
import cats.syntax.all._
import com.blmeadows.middleware.Tracing.trace
import io.chrisdavenport.fuuid.FUUID
import natchez.Trace
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.http4sLiteralsSyntax

trait FrogService[F[_]] {
  def getFrog(username: String, requestId: FUUID): F[Frog]
}

object FrogService {
  def apply[F[_]: Sync: Trace](C: Client[F]): FrogService[F] =
    new FrogService[F] {
      val dsl = new Http4sClientDsl[F] {}
      import dsl._

      override def getFrog(username: String, requestId: FUUID): F[Frog] =
        Trace[F].span("getFrog") {
          for {
            _ <- trace(requestId)
            frog <- C
              .expect[Frog](
                GET(uri"")
              ) // TODO: Find an api for getting random frog info or swap to a better idea :)
              .adaptError { case t => FrogError(t) } // Prevent Client Json Decoding Failure Leaking
          } yield frog
        }

    }
}
