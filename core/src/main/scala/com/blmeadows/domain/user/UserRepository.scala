package com.blmeadows.domain.user

import io.chrisdavenport.fuuid.FUUID

trait UserRepository[F[_]] {
  def create(user: User, requestId: FUUID): F[Unit]
  def read(username: String, requestId: FUUID): F[Option[User]]
  def update(user: User, requestId: FUUID): F[Unit]
  def delete(username: String, requestId: FUUID): F[Unit]
}
