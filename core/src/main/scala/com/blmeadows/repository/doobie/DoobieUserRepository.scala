package com.blmeadows.repository.doobie

import cats.effect.Sync
import cats.implicits._
import com.blmeadows.domain.user.{User, UserRepository}
import doobie.util.transactor.Transactor
import doobie.implicits._
import natchez.Trace
import com.blmeadows.middleware.Tracing.dbTrace
import io.chrisdavenport.fuuid.FUUID

object DoobieUserRepository {
  def impl[F[_]: Sync: Trace](transactor: Transactor[F]): UserRepository[F] = {
    new UserRepository[F] {
      override def create(user: User, requestId: FUUID): F[Unit] =
        Trace[F].span("database create") {
          dbTrace("INSERT INTO users", requestId).flatMap(_ =>
            sql"""INSERT INTO users (username, firstName, lastName) VALUES (${user.username}, ${user.firstName},${user.lastName})""".update.run
              .transact(transactor)
              .void
          )
        }

      override def read(username: String, requestId: FUUID): F[Option[User]] =
        Trace[F].span("database read") {
          dbTrace("SELECT FROM users", requestId).flatMap(_ =>
            sql"""SELECT username, firstName, lastName FROM users WHERE username = $username"""
              .query[User]
              .option
              .transact(transactor)
          )
        }

      override def update(user: User, requestId: FUUID): F[Unit] =
        Trace[F].span("database update") {
          dbTrace("UPDATE users", requestId).flatMap(_ =>
            sql"""UPDATE users (username, firstName, lastName) VALUES (${user.username}, ${user.firstName}, ${user.lastName})""".update.run
              .transact(transactor)
              .void
          )
        }

      override def delete(username: String, requestId: FUUID): F[Unit] =
        Trace[F].span("database delete") {
          dbTrace("DELETE FROM users", requestId).flatMap(_ =>
            sql"""DELETE FROM users WHERE username = $username""".update.run
              .transact(transactor)
              .void
          )
        }
    }
  }
}
