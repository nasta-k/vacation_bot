name := "vacation_bot"

version := "0.1"

scalaVersion := "2.13.6"

val catsVersion = "2.2.0"
val log4CatsVersion = "1.1.1"

libraryDependencies ++= Seq(
  "org.augustjune" %% "canoe" % "0.5.1",
  "com.github.pureconfig" %% "pureconfig" % "0.15.0",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.latestbit" %% "slack-morphism-client" % "3.1.0",
  "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % "2.2.9"
)