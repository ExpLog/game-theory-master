seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "game-theory-master"

version := "1.2"

scalaVersion := "2.10.4"

mainClass in Compile := Some("Main")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3"
)