import sbt._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / organization     := "com.fhuertas.uah.mbi"
ThisBuild / organizationName := "fhuertas"
ThisBuild / name             := "uah-mbi-2019"

lazy val root = project
  .in(file("."))
  .settings(settings)
