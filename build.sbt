val appName    = "corsign-core"
val libVersion = "0.1.0"
val scVersion  = "2.13.5"

lazy val core = project
  .in(file("core"))
  .settings(
    inThisBuild(
      List(
        resolvers += "jitpack" at "https://jitpack.io",
        name := appName,
        organization := "innFactory",
        version := libVersion,
        scalaVersion := scVersion,
        scalafmtOnCompile in compile := true
      )
    ),
    libraryDependencies ++=
      Seq(
        "com.typesafe.play"       %% "play-json"       % "2.9.2",
        "com.outr"                %% "hasher"          % "1.2.2",
        "com.github.kenglxn.qrgen" % "javase"          % "2.6.0",
        "com.nimbusds"             % "nimbus-jose-jwt" % "9.7",
        "org.bouncycastle"         % "bcpkix-jdk15on"  % "1.68",
        "commons-codec"            % "commons-codec"   % "1.15"
      )
  )

lazy val corsign = project
  .in(file("."))
  .dependsOn(core)
