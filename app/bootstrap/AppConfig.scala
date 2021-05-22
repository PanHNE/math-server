package bootstrap

import play.api.Configuration

import javax.inject.Inject

/**
 * Parametry aplikacji
 */
class AppConfig @Inject()(config: Configuration) {

  val adminEmail = config.getOptional[String]("app.email.admin").getOrElse("admin@math.pl")

  val noReply = config.getOptional[String]("play.mailer.email.no-reply").getOrElse("no-reply@hne.pl")

  val appName = config.get[String]("app.name")

  val appVersion = config.get[String]("app.version")

  val appProtocol = config.getOptional[String]("app.protocol").getOrElse("http://")

  val appDomain = config.getOptional[String]("app.domain").getOrElse("localhost")

  val fullAppDomain = appProtocol + appDomain

  val filePath = config.getOptional[String]("fileSystem.basePath").getOrElse("math_files/")
}
