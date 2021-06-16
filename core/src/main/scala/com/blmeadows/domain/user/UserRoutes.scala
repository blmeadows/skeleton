package com.blmeadows.domain.user

import cats.effect.Sync
import cats.syntax.all._
import com.blmeadows.middleware.Tracing.{mkRequestId, trace}
import io.chrisdavenport.log4cats.Logger
import io.circe.syntax._
import natchez.Trace
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.HttpRoutes

object UserRoutes {

  def routes[F[_]: Sync: Trace](logger: Logger[F], userService: UserService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] { // add auth and error handling
      case req @ POST -> Root / "user" =>
        Trace[F].span("POST user") {
          for {
            user <- req.as[User]
            requestId <- mkRequestId
            _ <- trace(requestId)
            _ <- userService
              .createUser(user, requestId)
              .handleErrorWith(error => logger.info(s"error is: $error"))
            resp <- NoContent()
          } yield resp
        }

      case GET -> Root / "user" / username =>
        for {
          requestId <- mkRequestId
          _ <- trace(requestId)
          userMaybe <- userService.getUser(username, requestId)
          resp <- userMaybe.fold(NotFound())(user => Ok(user.asJson))
        } yield resp

      case req @ PUT -> Root / "user" =>
        for {
          user <- req.as[User]
          requestId <- mkRequestId
          _ <- trace(requestId)
          _ <- userService.updateUser(user, requestId)
          resp <- NoContent()
        } yield resp

      case DELETE -> Root / "user" / username =>
        for {
          requestId <- mkRequestId
          _ <- trace(requestId)
          _ <- userService.deleteUser(username, requestId)
          resp <- NoContent()
        } yield resp
    }
  }
}
