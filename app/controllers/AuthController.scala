package controllers

import bootstrap.AppConfig
import com.mohiva.play.silhouette.api.{LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.auth.LoginForm
import models.User
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.UserService
import utils.auth.CookieEnvironment

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Kontroler zapytań obsługi autoryzacji
 */
class AuthController @Inject()(
    userService: UserService,
    components: ControllerComponents,
    silhouette: Silhouette[CookieEnvironment],
    credentialsProvider: CredentialsProvider
  )(
    implicit
    appConfig: AppConfig,
    assets: AssetsFinder,
    context: ExecutionContext
  ) extends AbstractController(components) with I18nSupport {

  /**
   * Przekierowanie do strony logowania
   */
  val Login: Result = Redirect(routes.AuthController.login)

  /**
   * Loguje do serwisu
   */
  def authenticate = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    LoginForm.form.bindFromRequest.fold(
      formWithErrors =>
        Future.successful(BadRequest(views.html.auth.login(formWithErrors))),

      data => {
        val credentials = Credentials(data.providerKey, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {

            case Some(user) =>
              silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
                  val result = Redirect(routes.HomeController.index)
                  silhouette.env.authenticatorService.embed(value, result)
                }
              }

            case None =>
              Future.failed(new IdentityNotFoundException(Messages("auth.not.found.user")))
          }
        }.recover {

          case _: ProviderException =>
            Login.flashing("error" -> Messages("auth.credentials.invalid"))
        }
      }
    )
  }

  /**
   * Wyświetla stronę logowania
   */
  def login = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.auth.login(LoginForm.form)))
  }

  /**
   * Wylogowuje z serwisu
   */
  def logout = silhouette.SecuredAction.async { implicit request: SecuredRequest[CookieEnvironment, AnyContent] =>
    implicit val loggedIn: User = request.identity
    silhouette.env.eventBus.publish(LogoutEvent(loggedIn, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Login)
  }

}
