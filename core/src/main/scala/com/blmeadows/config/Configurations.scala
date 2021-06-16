package com.blmeadows.config

private[config] object Configurations {

  final case class HttpConfig(host: String, port: Int)

  final case class PostgresConfig(
      jdbcUrl: String,
      username: String,
      password: String,
      driver: String,
      threadPoolSize: Int
  )

}
