name := "vacation_bot"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.latestbit" %% "slack-morphism-client" % "3.1.0",
  "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % "2.2.9"
)