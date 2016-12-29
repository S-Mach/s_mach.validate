
lazy val `validate-core` =
  project

lazy val validate =
  project
    .dependsOn(`validate-core`)

lazy val `validate-play_json` =
  project
    .dependsOn(`validate`)

lazy val `validate-example` =
  project
    .dependsOn(`validate-play_json`)