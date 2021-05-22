import com.typesafe.config.ConfigFactory

lazy val `math-server` = (project in file(".")).enablePlugins(PlayScala)
val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

scalaVersion := "2.12.8"
version := conf.getString("app.version")

name := "math-server"

packageDescription := """Math Server Application"""

maintainer := """Adrian Domin <adrian.domin@contact.pl>"""

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

libraryDependencies ++= Seq(
  jdbc % Test,
  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  logback,
  evolutions,
  akkaHttpServer,
  filters
)

libraryDependencies ++= Seq(
  // Quill
  "io.getquill" %% "quill-jdbc" % "3.7.0",

  // Sterownik Postgresa
  "org.postgresql" % "postgresql" % "42.2.19",

  // Bootstrap 4
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
  "org.webjars" % "bootstrap" % "4.6.0-1",

  // jQuery
  "org.webjars.bower" % "jquery" % "3.6.0",

  // Font Awesome
  "org.webjars" % "font-awesome" % "5.15.2",

  // Silhouette
  "com.mohiva" %% "play-silhouette" % "5.0.7",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.7",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.7",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.7",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.7" % Test,

  // Ficus wymagany do Silhouette
  "com.iheart" %% "ficus" % "1.4.6",

  // Scala Guice wymagany do Silhouette
  "net.codingwell" %% "scala-guice" % "4.2.4",

  // JQuery Easing Plugin
  "org.webjars" % "jquery-easing" % "1.4.1",

  // Webjars
  "org.webjars" %% "webjars-play" % "2.6.3",

  // Obsługa maili
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",

  // Scrmage
  "com.sksamuel.scrimage" %% "scrimage-core" % "3.0.0-alpha4"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
