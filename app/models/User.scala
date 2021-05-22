package models

import java.util.UUID
import io.getquill.Embedded
import utils.RoleType
import utils.RoleType.RoleType
import utils.auth.IdentitySilhouette

/**
 * Użytkownik
 *
 * @param id                  identyfikator użytkownika
 * @param providerId          potwierdzenie tożsamości
 * @param roleType            rola w systemie
 * @param providerKey         klucz uwierzytelniania - email użytkownika
 * @param password            hasło
 * @param firstName           imię
 * @param lastName            nazwisko
 * @param resetPasswordToken  token do zmiany hasła
 */
case class User(
 id: UUID,
 providerId: String,
 roleType: RoleType.Value,
 providerKey: String,
 password: String,
 firstName: Option[String],
 lastName: Option[String],
 phoneNumber: Option[String],
 resetPasswordToken: Option[String] = None,
) extends IdentitySilhouette with Embedded {

  /**
   * Adres email użytkownika
   */
  val email: String = providerKey

  /**
   * Imię i nazwisko
   */
  val fullName: String = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}"

  /**
   * Pełna nazwa użytkownika wraz z emailem
   */
  val fullNameWithEmail: String = s"$fullName <$email>"

  /**
   * True, jeżeli jest administratorem
   */
  def isAdmin = roleType == RoleType.ADMIN

  /**
   * True, jeżeli jest użytkownik ma role klienta
   */
  def isStudent = roleType == RoleType.CLIENT

  /**
   * True, jeżeli jest użytkownik ma role specjalisty
   */
  def isRecruiter = roleType == RoleType.SPECIALIST

}
