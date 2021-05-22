package forms.user

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.data.Form
import play.api.data.Forms._
import utils.FormFieldImplicits.`enum`
import utils.RoleType
import utils.RoleType.RoleType

import java.util.UUID

/**
 * Formularz danych tworzenia nowego użytkownika
 *
 * @param providerId          potwierdzenie tożsamości
 * @param roleType            rola w systemie
 * @param providerKey         klucz uwierzytelniania - email użytkownika
 * @param password            hasło
 * @param reEnterPassword     potwierdzenie hasła
 * @param firstName           imię
 * @param lastName            nazwisko
 */
case class UserForm(
   providerId: String,
   roleType: RoleType.Value,
   providerKey: String,
   password: String,
   reEnterPassword: String,
   firstName: Option[String],
   lastName: Option[String],
   phoneNumber: Option[String],
 ){
  /**
   * Wypełniony formularz razem z błędem
   *
   * @param message treść błędu
   *
   */
  def formWithGlobalError(message: String, args: Any*) : Form[UserForm] = {
    UserForm.form.fill(UserForm(providerId, roleType, providerKey, password, reEnterPassword, firstName, lastName, phoneNumber))
  }

  /**
   * Wypełniony formularz User - użytkownik
   *
   * @param user user do utworzenia formularza
   * @return Wypełniony formluarz danymi z user
   */
  def toUser(user: UserForm) : User = {
    User(
      UUID.randomUUID(),
      CredentialsProvider.ID,
      user.roleType,
      user.providerKey,
      user.password,
      user.firstName,
      user.lastName,
      user.phoneNumber
    )
  }
}

/**
 * Obiekt pomagjący w prarsowaniu i walidacji danych
 */
object UserForm {
  val form = Form(
    mapping(
      "providerId" -> nonEmptyText,
      "roleType" -> enum[RoleType](RoleType),
      "providerKey" -> nonEmptyText,
      "password" -> nonEmptyText,
      "reEnterPassword" -> nonEmptyText,
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "phoneNumber" -> optional(text)
    )(UserForm.apply)(UserForm.unapply)
      .verifying("auth.passwordReset.not-match", password =>
        password.password == password.reEnterPassword
      )
  )
}