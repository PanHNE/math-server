package daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import database.{DatabaseConnector, QueryExtension}
import models.User

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Zapytania bazodanowe dotyczące użytkowników
 */
class UserDAOImpl @Inject() (val database: DatabaseConnector) (implicit context: ExecutionContext)
  extends UserDAO with QueryExtension {

  import database.ctx._

  private val users = quote(querySchema[User]("users"))

  /**
   * Usuwa token zmiany hasła i restartuje datę ostatniej zmiany hasła
   *
   * @param user Użytkownik
   */
  override def clearResetPasswordToken(user: User): Future[User] = {
    run(users.filter(_.id == lift(user.id)).update(
      _.resetPasswordToken -> lift(None: Option[String])
    ))
    Future.successful(user)
  }

  /**
   * Ilość zarejestrowanych użytkowników.
   *
   * @return Liczbę zarejestrowanych użytkowników.
   */
  override def count: Future[Long] = Future.successful(run(users.size))

  /**
   * Usuwanie użytkownika z systemu
   *
   * @param userId identyfikator użytkownika do usunięcia
   * @return Informacje o błędzie w usuwanie uzytkownika lub None gdy użytkownik zostanie poprawnie usunięty
   */
  override def delete(userId: UUID): Future[Option[String]] = transaction {
    run(users.filter(_.id == lift(userId)).delete)

    Future.successful(None)
  }

  /**
   * Generuje token zmiany hasła użytkownika
   *
   * @param user Obiekt użytkownika
   * @return token zmiany hasła
   */
  override def generateResetPasswordToken(user: User): Future[String] = {
    val token: Option[String] = Some(UUID.randomUUID().toString)

    run(users.filter(_.id == lift(user.id)).update(_.resetPasswordToken -> lift(token)))

    Future.successful(token.get)
  }

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego emaila.
   *
   * @param email Email poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po emailu.
   */
  override def retrieve(email: String): Future[Option[User]] = {
    Future.successful(run(users.filter(_.providerKey == lift(email))).headOption)
  }

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego identyfikatora.
   *
   * @param id Identyfikator poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po identyfikatorze.
   */
  override def retrieve(id: UUID): Future[Option[User]] = {
    Future.successful(run(users.filter(_.id == lift(id))).headOption)
  }

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do informacjami autoryzacji logowania.
   *
   * @param loginInfo Informacje autoryzacji logowania poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po informacjach dotyczących logowania.
   */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = Future.successful(
    run(users.filter { data =>
      (data.providerId == lift(loginInfo.providerID)) && data.providerKey.compareLowerCase(lift(loginInfo.providerKey))
    }).headOption
  )

  /**
   * Zapisuje użytkownika.
   *
   * @param user Użytkownik do zapisania.
   * @return Zapisuje użytkownika lub zwraca None gdy użytkownik istnieje.
   */
  override def save(userData: User): Future[Option[User]] = transaction {
    isUnique(userData).map { isUnique =>
      if (isUnique) {
        run(users.insert(lift(userData)))
        Some(userData)
      } else
        None
    }
  }

  /**
   * Zapisuje użytkownika
   *
   * @param user dane użytkownika
   * @return left - komunikat błędu, right - dodany użytkownik
   */
  override def register(user: User)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Either[String, User]] = for {
    userByLogin <- retrieve(user.loginInfo)
  } yield {
    if(userByLogin.nonEmpty) {
      Left("auth.signUp.failure.userEmailExists")
    } else {
      run(users.insert(lift(user)))
      Right(user)
    }
  }

  /**
   * Aktualizuje dane użytkownika
   *
   * @param user Użytkownik z danymi do aktualizacji.
   * @return Użytkownik z zaktualizowanymi danymi.
   */
  override def update(user: User): Future[User] = transaction {
    run(users.filter(_.id == lift(user.id)).update(
      _.roleType -> lift(user.roleType),
      _.firstName -> lift(user.firstName),
      _.lastName -> lift(user.lastName),
      _.phoneNumber -> lift(user.phoneNumber)
    ))

    Future.successful(user)
  }

  /**
   * Aktualizuje hasło
   *
   * @param user     Użytkownik któremu należy zaktualizować hasło
   * @param password Hash hasła
   * @return Zaktualizowany użytkownik
   */
  override def updatePassword(user: User, password: String): Future[Option[User]] = transaction {
    run(users.filter(_.id == lift(user.id)).update(_.password -> lift(password)))

    Future.successful(Some(user.copy(password = password)))
  }

  /**
   * Sprawdza, czy wprowadzone dane nie są już zapisane w bazie
   *
   * @param userData dane użytkownika
   * @return true jeżeli nie istnieje w bazie użytkownik o podanych danych
   */
  private def isUnique(userData: User) = for {
    userByLogin <- retrieve(userData.loginInfo)
  } yield userByLogin.isEmpty
}
