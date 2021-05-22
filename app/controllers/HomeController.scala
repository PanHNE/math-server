package controllers

import bootstrap.AppConfig
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.I18nSupport

import javax.inject._
import play.api.mvc._
import utils.auth.CookieEnvironment

import scala.concurrent.ExecutionContext

/**
 * Kontroler głównej strony aplikacji
 */
@Singleton
class HomeController @Inject()(
                                components: ControllerComponents,
                                silhouette: Silhouette[CookieEnvironment]
                              )(
                                implicit
                                appConfig: AppConfig,
                                assets: AssetsFinder,
                                context: ExecutionContext
                              ) extends AbstractController(components) with I18nSupport {

  /**
   * Główma strona aplikacji
   */
  def index = silhouette.UserAwareAction { implicit request =>
    request.identity.map { implicit loggedIn =>
      Ok(views.html.index("Strona główna"))
    }.getOrElse {
      Redirect(routes.AuthController.login)
    }
  }
}
