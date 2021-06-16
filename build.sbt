// Scala
val scalaV = "2.13.6"
val circeV = "0.14.1"
val pureConfigV = "0.15.0"
val epimetheusV = "0.4.0"
val epimetheusHttp4sV = "0.4.2"
val betterMonadicForV = "0.3.1"
val fuuidV = "0.6.0"
val specs2V = "4.12.0"

// Typelevel Scala
val catsV = "2.6.1"
val catsEffectV = "2.5.1"
val fs2V = "2.5.6"
val http4sV = "0.21.24"
val natchezV = "0.0.26"
val natchezHttp4sV = "0.0.3"
val doobieV = "0.13.4"
val log4catsV = "1.1.1"
val kindProjectorV = "0.13.0"
val munitCatsEffectV = "1.0.3"

// Scalameta
val munitV = "0.7.26"

// Java
val flyWayV = "7.9.1"
val logbackClassicV = "1.2.3"
val logstashEncoderV = "6.6"

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(name := "skeleton")

//lazy val docs = project.in(file("docs"))
//  .dependsOn(core)
//  .settings(commonSettings)

lazy val commonSettings = Seq(
  organization := "com.blmeadows",
  scalaVersion := scalaV,
  publishArtifact := false, // https://www.scala-sbt.org/1.x/docs/Artifacts.html#Modifying+default+artifacts
  Global / cancelable := true, // cancel running task without existing sbt
  addCompilerPlugin(
    "org.typelevel" % "kind-projector" % kindProjectorV cross CrossVersion.full
  ), // https://github.com/typelevel/kind-projector
  addCompilerPlugin(
    "com.olegpy" %% "better-monadic-for" % betterMonadicForV
  ), // https://github.com/oleg-py/better-monadic-for
  libraryDependencies ++= Seq(
    // Functional programming library
    "org.typelevel" %% "cats-core" % catsV, // https://typelevel.org/cats/
    // Pure asynchronous runtime
    "org.typelevel" %% "cats-effect" % catsEffectV, // https://typelevel.org/cats-effect/
    // Functional, effectful, concurrent streams
    "co.fs2" %% "fs2-io" % fs2V, // https://fs2.io
    // Typeful, functional, streaming HTTP
    "org.http4s" %% "http4s-dsl"          % http4sV, // https://http4s.org/v0.21/dsl/
    "org.http4s" %% "http4s-ember-server" % http4sV, // https://github.com/http4s/http4s/blob/main/examples/ember/src/main/scala/com/example/http4s/ember/EmberServerSimpleExample.scala
    "org.http4s" %% "http4s-ember-client" % http4sV, // https://github.com/http4s/http4s/blob/main/examples/ember/src/main/scala/com/example/http4s/ember/EmberClientSimpleExample.scala
    // Metrics
    "org.http4s"        %% "http4s-prometheus-metrics" % http4sV, // https://http4s.org/v0.21/middleware/
    "io.chrisdavenport" %% "epimetheus"                % epimetheusV, // https://github.com/davenverse/epimetheus
    "io.chrisdavenport" %% "epimetheus-http4s"         % epimetheusHttp4sV, // https://github.com/davenverse/epimetheus-http4s
    // Functional UUID
    "io.chrisdavenport" %% "fuuid" % fuuidV, // https://davenverse.github.io/fuuid/
    // Config
    "com.github.pureconfig" %% "pureconfig"        % pureConfigV, // https://pureconfig.github.io/
    "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigV, // https://github.com/pureconfig/pureconfig/tree/master/modules/http4s
    // JSON
    "org.http4s" %% "http4s-circe"  % http4sV, // https://http4s.org/v0.21/json/
    "io.circe"   %% "circe-generic" % circeV, // https://circe.github.io/circe/
    "io.circe"   %% "circe-parser"  % circeV, // https://circe.github.io/circe/
    // Logging
    "io.chrisdavenport"   %% "log4cats-slf4j"           % log4catsV, // https://typelevel.org/log4cats/
    "ch.qos.logback"       % "logback-classic"          % logbackClassicV, // http://logback.qos.ch/
    "net.logstash.logback" % "logstash-logback-encoder" % logstashEncoderV, // https://github.com/logstash/logstash-logback-encoder
    // Tracing
    "org.tpolecat" %% "natchez-core"   % natchezV, // https://tpolecat.github.io/natchez/
    "org.tpolecat" %% "natchez-jaeger" % natchezV, // https://github.com/tpolecat/natchez/tree/series/0.0/modules/jaeger/src/main/scala
    "org.tpolecat" %% "natchez-http4s" % natchezHttp4sV, // https://tpolecat.github.io/natchez-http4s/
    // Database
    "org.tpolecat" %% "doobie-core"     % doobieV, // https://tpolecat.github.io/doobie/
    "org.tpolecat" %% "doobie-hikari"   % doobieV, // https://github.com/tpolecat/doobie/tree/master/modules/hikari/src
    "org.tpolecat" %% "doobie-postgres" % doobieV, // https://github.com/tpolecat/doobie/tree/master/modules/postgres/src
    "org.flywaydb"  % "flyway-core"     % flyWayV, // https://flywaydb.org/
    // Testing
    "org.scalameta" %% "munit"               % munitV           % Test, // https://scalameta.org/munit/
    "org.typelevel" %% "munit-cats-effect-2" % munitCatsEffectV % Test, // https://github.com/typelevel/munit-cats-effect
    "org.tpolecat"  %% "doobie-munit"        % doobieV          % Test // https://github.com/tpolecat/doobie/tree/master/modules/munit/src
  ) // TODO: swap doobie to skunk
)
