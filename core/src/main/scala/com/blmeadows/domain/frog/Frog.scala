package com.blmeadows.domain.frog

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

final case class Frog(
    Family: String,
    Species: String
)

object Frog {
  implicit val decoder: Decoder[Frog] = deriveDecoder[Frog]
  implicit val encoder: Encoder[Frog] = deriveEncoder[Frog]
  implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, Frog] = jsonOf
  implicit def entityEncoder[F[_]]: EntityEncoder[F, Frog] = jsonEncoderOf
}

final case class FrogError(e: Throwable) extends RuntimeException
