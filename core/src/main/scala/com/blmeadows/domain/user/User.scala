package com.blmeadows.domain.user

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

final case class User(
    username: String,
    firstName: String,
    lastName: String
)

object User {
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]
  implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, User] = jsonOf
  implicit def entityEncoder[F[_]]: EntityEncoder[F, User] = jsonEncoderOf
}
