name := "game-theory-master"

version := "0.1"

scalaVersion := "2.11.1"

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits", "-diagrams", "-feature")

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
	"org.scala-lang" %% "scala-pickling" % "0.8.0"  withSources() withJavadoc()
)