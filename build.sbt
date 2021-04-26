
val appName    = "corsign-core"
val libVersion = "1.1.3"
val scVersion  = "2.13.5"

name := appName

val token = sys.env.getOrElse("GITHUB_TOKEN", "")

credentials :=
  Seq(
    Credentials(
      "GitHub Package Registry",
      "maven.pkg.github.com",
      "innFactory",
      token
    )
  )

val defaultProjectSettings = Seq(
  scalaVersion := scVersion,
  organization := "de.innfactory.corsign-core",
  version := libVersion,
  githubOwner := "innFactory",
  githubRepository := appName,
  githubTokenSource := TokenSource.GitConfig("github.token") || TokenSource.Environment("GITHUB_TOKEN")
)

lazy val core = project
  .in(file("core"))
  .settings(
    defaultProjectSettings,
    name := "core",
    inThisBuild(
      List(
        resolvers += "jitpack" at "https://jitpack.io",
        name := "core",
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
        "commons-codec"            % "commons-codec"   % "1.15",
        "org.scalacheck"         %% "scalacheck"              % "1.15.2"  % Test,
        "org.scalatest"          %% "scalatest"               % "3.2.6"   % Test,
        "org.scalatestplus"      %% "scalacheck-1-14"         % "3.2.2.0" % Test
      )
  )

lazy val corsign = project
  .in(file("."))
  .settings(defaultProjectSettings)
  .dependsOn(core)
  .aggregate(core)

addCommandAlias("coreTest",";project core; clean; coverage ;test ;coverageReport")