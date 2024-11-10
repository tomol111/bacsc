val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "bacsc",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    //libraryDependencies += "org.jline" % "jline" % "3.21",
	libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10",
	libraryDependencies += "dev.dirs" % "directories" % "26",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  )
