package services

import bootstrap.AppConfig
import controllers.routes
import models.User
import play.api.i18n.MessagesApi
import play.api.libs.mailer.{Email, MailerClient}

import javax.inject.Inject

/**
 * Serwis do wysyłania maili
 */
class MailerService @Inject()(
                               mailerClient: MailerClient,
                               appConfig: AppConfig,
                               messagesApi: MessagesApi
                             ) {


  /**
   * Wysyła wiadomość z linkiem do zmiany hasła uzytkownikowi który zapomniał hasła
   *
   * @param recipient adresat wiadomości
   */
  def sendPasswordResetToken(recipient: User, resetPasswordToken: String) = {
    val email = Email(
      subject = "PM - zmiana hasła",
      from = s"${appConfig.appName} <${appConfig.noReply}>",
      to = Seq(s"${recipient.fullName} <${recipient.email}>"),
      bodyText = Some(
        s"""Hasło zmienisz pod adresem: ${appConfig.fullAppDomain}${routes.UserController.resetPasswordWithoutLogin(recipient.id, resetPasswordToken)}"""
          .stripMargin)
    )
    mailerClient.send(email)
  }
}
