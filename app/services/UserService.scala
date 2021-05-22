package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import models.User

import java.util.UUID
import scala.concurrent.Future

/**
 * Obsługuje wszystkie zapytania użytkowników
 */
trait UserService extends IdentityService[User] {

  /**
   * Ilość zarejestrowanych użytkowników.
   *
   * @return Liczbę zarejestrowanych użytkowników.
   */
  def count: Future[Long]

  /**
   * Usuwanie użytkownika z systemu
   *
   * @param userId identyfikator użytkownika do usunięcia
   * @return Informacje o błędzie w usuwanie uzytkownika lub None gdy użytkownik zostanie poprawnie usunięty
   */
  def delete(userId: UUID): Future[Option[String]]

  /**
   * Generuje token zmiany hasła użytkownika
   *
   * @param user Obiekt użytkownika
   * @return token zmiany hasła
   */
  def generateResetPasswordToken(user: User): Future[String]

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego emaila.
   *
   * @param email Email poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po emailu.
   */
  def retrieve(email: String): Future[Option[User]]

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego identyfikatora.
   *
   * @param id Identyfikator poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po identyfikatorze.
   */
  def retrieve(id: UUID): Future[Option[User]]

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do informacjami autoryzacji logowania.
   *
   * @param loginInfo Informacje autoryzacji logowania poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po informacjach dotyczących logowania.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Zapisuje użytkownika.
   *
   * @param user Użytkownik do zapisania.
   * @return Zapisuje użytkownika lub zwraca None gdy użytkownik istnieje.
   */
  def save(user: User): Future[Option[User]]

  /**
   * Zapisuje użytkownika
   *
   * @param user dane użytkownika
   * @return left - komunikat błędu, right - dodany użytkownik
   */
  def register(user: User)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Either[String, User]]

  /**
   * Aktualizuje dane użytkownika
   *
   * @param user Użytkownik z danymi do aktualizacji.
   * @return Użytkownik z zaktualizowanymi danymi.
   */
  def update(user: User): Future[User]

  /**
   * Aktualizuje hasło
   *
   * @param user     Użytkownik któremu należy zaktualizować hasło
   * @param password Hash hasła
   * @return Zaktualizowany użytkownik
   */
  def updatePassword(user: User, password: String): Future[Option[User]]

  /**
   * Aktualizuje hasło użytkownika
   *
   * @param user      użytkownik
   * @param token     token do zmiany hasła
   * @param password  nowe hasło
   * @return użytkownik jeżeli odnaleziono
   */
  def updatePassword(user: User, token: String, password: String)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Option[User]]

  /**
   * Aktualizuje hasło użytkownika
   *
   * @param userId   identyfikator użytkownika
   * @param token    token autoryzacji zmiany hasła
   * @param password nowe hasło
   * @return Zaktualizowany użytkownik
   */
  def updatePasswordWithoutLogin(userId: UUID, token: String, password: String)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Option[User]]

}
