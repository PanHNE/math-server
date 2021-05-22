package forms.user

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import utils.FormFieldImplicits.`enum`
import utils.RoleType
import utils.RoleType.RoleType

/**
 * Formularz danych edytowania nowego użytkownika
 *
 * @param roleType            rola w systemie
 * @param providerKey         klucz uwierzytelniania - email użytkownika
 * @param firstName           imię
 * @param lastName            nazwisko
 */
case class EditUserForm(
 roleType: RoleType.Value,
 providerKey: Option[String],
 firstName: Option[String],
 lastName: Option[String],
 phoneNumber: Option[String]
){

  /**
   * Wypełniony formularz User - użytkownik
   *
   * @param user user do utworzenia formularza
   * @return Wypełniony formluarz danymi z user
   */
  def toUser(userForm: EditUserForm, user: User) : User = {
    User(
      user.id,
      CredentialsProvider.ID,
      userForm.roleType,
      user.email,
      user.providerKey,
      userForm.firstName,
      userForm.lastName,
      userForm.phoneNumber
    )
  }
}

/**
 * Obiekt pomagjący w prarsowaniu i walidacji danych
 */
object EditUserForm{
  val form = Form(
    mapping(
      "roleType" -> enum[RoleType](RoleType),
      "providerKey" -> optional(text),
      "firstName" -> optional(nonEmptyText),
      "lastName" -> optional(text),
      "phoneNumber" -> optional(text)
    )(EditUserForm.apply)(EditUserForm.unapply)
  )

}
