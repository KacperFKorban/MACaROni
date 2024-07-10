val scala3 = "3.3.3"

val commonSettings = Seq(
  organization := "io.github.kacperfkorban",
  description := "Common Scala 3 macro utils",
  homepage := Some(url("https://github.com/KacperFKorban/MACaROni")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "KacperFKorban",
      "Kacper Korban",
      "kacper.f.korban@gmail.com",
      url("https://twitter.com/KacperKorban")
    )
  ),
  scalaVersion := scala3,
  scalacOptions ++= Seq(
    // "-Xcheck-macros",
    // "-Ycheck:inlining",
    "-explain",
    "-deprecation",
    "-unchecked",
    "-feature"
  ),
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % "1.0.0-M6" % Test
  )
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "macaroni-root",
    publish / skip := true
  )
  .aggregate((macaroni.projectRefs): _*)

lazy val macaroni = projectMatrix
  .in(file("macaroni"))
  .settings(commonSettings)
  .settings(
    name := "MACaROni",
    libraryDependencies ++= Seq(
      "com.softwaremill.quicklens" %%% "quicklens" % "1.9.7"
    ),
    Test / scalacOptions ++= Seq(
      "-Xfatal-warnings" // we test that unchecked is applied correctly
    )
  )
  .jvmPlatform(scalaVersions = List(scala3))
