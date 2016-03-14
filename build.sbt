sbtVersion := "0.13.5"

val nexus = "https://oss.sonatype.org/"
val nexusSnapshots = "snapshots" at nexus + "content/repositories/snapshots"
val nexusReleases = "releases"  at nexus + "service/local/staging/deploy/maven2"

// TODO: figure out how to move this into publish.sbt
val publishSettings = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  publishTo := {
    if (isSnapshot.value)
      Some(nexusSnapshots)
    else
      Some(nexusReleases)
  },
  pomExtra :=
    <url>https://github.com/S-Mach/s_mach.validate</url>
      <licenses>
        <license>
          <name>MIT</name>
          <url>http://opensource.org/licenses/MIT</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:S-Mach/s_mach.validate.git</url>
        <connection>scm:git:git@github.com:S-Mach/s_mach.validate.git</connection>
        <developerConnection>scm:git:git@github.com:S-Mach/s_mach.validate.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <id>lancegatlin</id>
          <name>Lance Gatlin</name>
          <email>lance.gatlin@gmail.com</email>
          <organization>S-Mach</organization>
          <organizationUrl>http://s-mach.net</organizationUrl>
        </developer>
      </developers>
)

val defaultSettings = Defaults.coreDefaultSettings ++ publishSettings ++ Seq(
  scalaVersion := "2.11.7",
  organization := "net.s_mach",
  version := "2.0.0",
  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-deprecation"
  )
)

val test = Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)

// Note: multi-project build required since can't use macros in same
// jar as they are declared
lazy val validate = Project(
  id = "validate",
  base = file("."),
  aggregate = Seq(
    validateCore
  ),
  dependencies = Seq("validate-core")
)
  .settings(defaultSettings: _*)
  .settings(unidocSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "net.s_mach" %% "codetools-play_json" % "2.0.0" % "test",
    "net.s_mach" %% "explain_play_json" % "1.0.0" % "test",
    "net.s_mach" %% "validate-play_json" % "2.0.0" % "test",
    "com.github.fge" % "json-schema-validator" % "2.2.6" % "test"
  ) ++ test)

lazy val validateCore = Project(
  id = "validate-core",
  base = file("validate-core")
)
  .settings(defaultSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "net.s_mach" %% "string" % "1.1.0",
    "net.s_mach" %% "metadata" % "1.0.0",
    "net.s_mach" %% "codetools" % "2.0.0"
  ) ++ test)
