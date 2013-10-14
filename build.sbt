// Include the Android plugin
androidDefaults

// Name of your app
name := "Androxec"

// Version of your app
version := "0.1"

// Version number of your app
versionCode := 0

// Version of Scala
scalaVersion := "2.10.1"

// Version of the Android platform SDK
platformName := "android-16"

libraryDependencies ++= Seq(
   "ch.boye" %% "httpclientandroidlib" % "1.1.0" from "https://httpclientandroidlib.googlecode.com/files/httpclientandroidlib-1.1.0.jar", 
    "org.scalaj" %% "scalaj-collection" % "1.5",
    "net.liftweb" %% "lift-json" % "2.5.1"
    )
