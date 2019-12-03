name := """githubrank"""
organization := "com.github.ivankliuk"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(guice,
  ws,
  ehcache,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  "io.monix" %% "monix" % "3.1.0"
)

fork in run := true

// Change to default sbt layout
disablePlugins(PlayLayoutPlugin)
PlayKeys.playMonitoredFiles ++= (sourceDirectories in Compile).value
