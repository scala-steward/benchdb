lazy val root = project.in(file("."))
  .aggregate(core, plugin)
  .dependsOn(core)
  .settings(inThisBuild(Seq(
    organization := "com.lightbend.benchdb",
    //version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.14",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
  )))
  .settings(
    publishArtifact := false,
    publish := {},
    publishLocal := {},
  )

lazy val core = project.in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "benchdb-core",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "com.lightbend.benchdb",
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "6.10.0.202406032230-r",
      "com.monovore" %% "decline" % "2.4.1",
      "com.github.pathikrit" %% "better-files" % "3.9.2",
      "com.typesafe.slick" %% "slick" % "3.5.1",
      "com.mysql" % "mysql-connector-j" % "8.4.0" % "optional",
      "com.h2database" % "h2" % "2.2.224" % "optional",
      "com.typesafe" % "config" % "1.4.3",
      "org.slf4j" % "slf4j-api" % "2.0.13",
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "com.github.sbt" % "junit-interface" % "0.13.3" % "test"
    ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    Test / fork := true,
    Test / parallelExecution := false
  )

lazy val plugin = project.in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "sbt-benchdb",
    sbtPlugin := true,
    scalaVersion := "2.12.20",
    buildInfoKeys := Seq[BuildInfoKey](organization, core / name, version, core / scalaVersion),
    buildInfoPackage := "com.lightbend.benchdb.sbtplugin",
    publishMavenStyle := false,
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    scriptedDependencies := { val _ = ((core / publishLocal).value, publishLocal.value) },
  )
