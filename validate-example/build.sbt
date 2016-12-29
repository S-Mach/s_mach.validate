scalacOptions ~= (_ filterNot Set("-Ywarn-unused-import"))

libraryDependencies ++= Seq(
  "net.s_mach" %% "codetools-play_json" % "2.1.0" % "test",
  "net.s_mach" %% "explain_play_json" % "1.0.0" % "test",
//  "net.s_mach" %% "validate-play_json" % "2.0.0" % "test",
  "com.github.fge" % "json-schema-validator" % "2.2.6" % "test"
)
