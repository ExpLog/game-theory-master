name := "game-theory-master"

version := "0.1"

scalaVersion := "2.10.4"

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits", "-diagrams", "-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3"
)