val appName = "corsign-core"
val appVersion = "0.1.0"
val scVersion = "2.13.5"


lazy val core = project.in(file("core"))
  .settings(
  inThisBuild(
    List(
      resolvers += "jitpack" at "https://jitpack.io",
      name := appName,
      organization := "innFactory",
      version := appVersion,
      scalaVersion := scVersion
    )
  ),
  libraryDependencies ++=
    Seq(
      "com.typesafe.play" %% "play-json" % "2.9.2",
      "com.github.kenglxn.qrgen" % "javase" % "2.6.0",
      "com.nimbusds"            % "nimbus-jose-jwt"         % "9.7",
      "com.outr" %% "hasher" % "1.2.2"
    )

)


lazy val corsign = project
  .in(file("."))
  .dependsOn(core)

