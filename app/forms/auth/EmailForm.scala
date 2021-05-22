package forms.auth

import play.api.data.Form
import play.api.data.Forms._

/**
 * Formularz rządania zmiany hasła
 *
 * @param email adres email
 */
case class EmailForm(
  email: String
)

/**
 * Obiekt pomagjący w prarsowaniu i walidacji danych
 */
object EmailForm {
  val form = Form(
    mapping(
      "email" -> nonEmptyText
    )(EmailForm.apply)(EmailForm.unapply)
  )
}
