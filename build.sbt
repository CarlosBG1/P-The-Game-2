ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "P The Game 2"
  )

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

