scalaVersion := "2.11.6"

organization := "net.s_mach"

name := "validate"

version := "1.0.0-SNAPSHOT"

scalacOptions ++= Seq("-feature","-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.8",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)