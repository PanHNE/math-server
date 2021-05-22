package forms.auth

import play.api.data.Form
import play.api.data.Forms._

/**
 * Klasa przypadku - formularza logowania
 *
 * @param providerKey       email użytkownika
 * @param password    hasło użytkownika
 */
case class LoginForm(
  providerKey: String,
  password: String
)

/**
 * Obiekt pomagjący w prarsowaniu i walidacji danych
 */
object LoginForm {
  val form = Form(
    mapping(
      "providerKey" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )
}
