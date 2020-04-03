import com.typesafe.sbt.SbtGit.GitKeys._

lazy val commonSettings = Seq(
  organization := "com.alexitc",
  scalacOptions ++= Seq(
    "-Xlint",
    "-deprecation",
    "-Xfatal-warnings",
    "-feature"
  ), unmanagedSourceDirectories in Compile ++= Seq(
    baseDirectory.value.getParentFile / "shared" / "src" / "main" / "scala"
  ),
  scmInfo := Some(
    ScmInfo(
      url("http://github.com/AlexITC/scala-js-chrome"),
      "scm:git@github.com:AlexITC/scala-js-chrome.git"
    )
  ),
  developers := List(
    Developer(
      "AlexITC",
      "Alexis Hernandez",
      "alexis22229@gmail.com",
      url("https://wiringbits.net")
    )
  ),
  licenses += "MIT" -> url("http://www.opensource.org/licenses/mit-license.html"),
  homepage := Some(url("http://github.com/AlexITC/scala-js-chrome")),
  useGpg := true,
  useGitDescribe := true
)

lazy val commonPlugins = Seq(GitVersioning)

lazy val bindings = project.in(file("bindings"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-js-chrome",
    scalaVersion := "2.12.10",
  //  crossScalaVersions := Seq("2.10.6", "2.11.12", "2.12.7"), TODO: cross-compile to scala 2.13
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "com.lihaoyi" %%% "upickle" % "1.0.0"
    ),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    scalaJSUseMainModuleInitializer := true
  ).
  enablePlugins(commonPlugins: _*).
  enablePlugins(ScalaJSPlugin).
  enablePlugins(JSDependenciesPlugin)

lazy val plugin = project.in(file("sbt-plugin")).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-chrome-plugin",
    libraryDependencies ++= {
      // NOTE: Avoid circe as it doesn't respect binary compatibility which causes lots of issues
      Seq(
        "com.lihaoyi" %%% "upickle" % "1.0.0",
      "org.scalactic" %% "scalactic" % "3.1.1",
      "org.scalatest" %% "scalatest" % "3.1.1" % "test"
      )
    },
    publishMavenStyle := false,
    bintrayRepository := "sbt-plugins",
    bintrayOrganization := None,
    addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.0.1"),
    addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.0")
  ).
  enablePlugins(commonPlugins: _*)
