package controllers

import bootstrap.AppConfig
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import forms.auth.{EmailForm, ResetPasswordForm}
import forms.user.{EditUserForm, UserForm}
import models.User
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.{MailerService, UserService}
import utils.auth.CookieEnvironment

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Kontroler użytkowników
 */

class UserController @Inject()(
                                components: ControllerComponents,
                                mailerService: MailerService,
                                silhouette: Silhouette[CookieEnvironment],
                                userService: UserService
                              )(
                                implicit
                                appConfig: AppConfig,
                                assets: AssetsFinder,
                                context: ExecutionContext,
                                authInfoRepository: AuthInfoRepository,
                                passwordHasherRegistry: PasswordHasherRegistry,
                                webJarsUtil: WebJarsUtil
                              ) extends AbstractController(components) with I18nSupport {

  /**
   * Przekeirowanie do strony logowania
   */
  val Index: Result = Redirect(routes.HomeController.index)

  /**
   * Przekeirowanie do strony logowania
   */
  val Login: Result = Redirect(routes.AuthController.login)

  /**
   * Rejestracja nowego użytkownika
   */
  def register = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.auth.register(UserForm.form)))
  }

  /**
   * Formularz zmiany hasła użytkownika gdy użytkownik nie jest zalogowany i chce zmienić hasło
   */
  def resetPasswordWithoutLogin(id: UUID, token: String) = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(views.html.auth.resetPasswordWithoutLogin(id, token, ResetPasswordForm.form))
  }

  /**
   * Formularz prośby o zmianę hasła
   */
  def requestResetPassword = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(views.html.auth.requestResetPassword(EmailForm.form))
  }

  /**
   * Zapisywanie nowego użytkownika
   */
  def save = silhouette.UnsecuredAction.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.auth.register(formWithErrors))),

      data => {
        val userData = data.toUser(data)
        for {
          user <- userService.register(userData)
          _ <- authInfoRepository.add(userData.loginInfo, passwordHasherRegistry.current.hash(userData.password))
        } yield user.map { user =>
          userService.generateResetPasswordToken(user)
          Index.flashing("success" -> Messages("user.create.success", user.providerKey))
        }.getOrElse {
          BadRequest(views.html.auth.register(data.formWithGlobalError("users.create.exists", userData.providerKey)))
        }
      }
    )
  }

  /**
   * Zmiana hasła użytkownika gdy użytkownik nie jest zalogowany i chce zmienić hasło
   */
  def savePasswordWithoutLogin(id: UUID, token: String) = silhouette.UnsecuredAction.async  { implicit request: Request[AnyContent] =>
    ResetPasswordForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.auth.resetPasswordWithoutLogin(id, token, formWithErrors))),

      data => {
        userService.updatePasswordWithoutLogin(id, token, data.password).map {
          case Some(user) =>
            Login.flashing("success" -> Messages("user.reset.password.success"))

          case None =>
            Login.flashing("failure" -> Messages("user.reset.password.failure"))
        }
      }
    )
  }

  /**
   * Zmiana hasła użytkownika bez logowania czyli podczas zmiany gdy użytkownik zapomni hasła
   */
  def sendResetPasswordWithoutLogin = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    EmailForm.form.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.auth.requestResetPassword(formWithErrors)),

      data => {
        val result = for {
          optionalUser <- userService.retrieve(data.email)
        } yield for {
          user <- optionalUser
        } yield for {
          resetPasswordToken <- userService.generateResetPasswordToken(user)
        } yield mailerService.sendPasswordResetToken(user, resetPasswordToken)
        Login.flashing("success" -> Messages("auth.requestResetPassword.success", data.email))
      }
    )
  }
}
