package forms.auth

import play.api.data.Form
import play.api.data.Forms._

/**
 * Formularz danych zmiany hasła
 *
 * @param password         hasło
 * @param reEnterPassword  powtórzone hasło
 */
case class ResetPasswordForm (
 password: String,
 reEnterPassword: String
)

/**
 * Obiekt pomagjący w prarsowaniu i walidacji danych
 */
object ResetPasswordForm {
  val form = Form(
    mapping(
      "password" -> nonEmptyText,
      "reEnterPassword" -> nonEmptyText
    )(ResetPasswordForm.apply)(ResetPasswordForm.unapply)
      .verifying("auth.passwordReset.not-match", passwords =>
        passwords.password == passwords.reEnterPassword
      )
  )
}
