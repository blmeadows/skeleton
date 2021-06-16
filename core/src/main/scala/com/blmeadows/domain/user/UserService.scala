package com.blmeadows.domain.user

import cats.effect.Sync
import cats.syntax.all._
import io.chrisdavenport.fuuid.FUUID
import com.blmeadows.middleware.Tracing.trace
import natchez.Trace

trait UserService[F[_]] {
  def createUser(user: User, requestId: FUUID): F[Unit]
  def getUser(username: String, requestId: FUUID): F[Option[User]]
  def updateUser(user: User, requestId: FUUID): F[Unit]
  def deleteUser(username: String, requestId: FUUID): F[Unit]
}

object UserService {
  def apply[F[_]: Sync: Trace](userRepo: UserRepository[F]): UserService[F] =
    new UserService[F] {
      override def createUser(user: User, requestId: FUUID): F[Unit] =
        Trace[F].span("createUser") {
          for {
            _ <- trace(requestId)
            _ <- userRepo.create(user, requestId)
          } yield ()
        }

      override def getUser(username: String, requestId: FUUID): F[Option[User]] =
        Trace[F].span("getUser") {
          for {
            _ <- trace(requestId)
            user <- userRepo.read(username, requestId)
          } yield user
        }

      override def updateUser(user: User, requestId: FUUID): F[Unit] =
        Trace[F].span("updateUser") {
          for {
            _ <- trace(requestId)
            _ <- userRepo.update(user, requestId)
          } yield ()
        }

      override def deleteUser(username: String, requestId: FUUID): F[Unit] =
        Trace[F].span("deleteUser") {
          for {
            _ <- trace(requestId)
            _ <- userRepo.delete(username, requestId)
          } yield ()
        }
    }
}
