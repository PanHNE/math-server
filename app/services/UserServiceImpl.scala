package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import daos.UserDAO
import models.User
import utils.FutureUtils

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Handles actions to users.
 */
class UserServiceImpl @Inject()(
                                 userDAO: UserDAO,
                               )(implicit context: ExecutionContext) extends UserService {
  /**
   * Ilość zarejestrowanych użytkowników.
   *
   * @return Liczbę zarejestrowanych użytkowników.
   */
  override def count: Future[Long] = userDAO.count

  /**
   * Usuwanie użytkownika z systemu
   *
   * @param userId identyfikator użytkownika do usunięcia
   * @return Informacje o błędzie w usuwanie uzytkownika lub None gdy użytkownik zostanie poprawnie usunięty
   */
  override def delete(userId: UUID): Future[Option[String]] = userDAO.delete(userId)

  /**
   * Generuje token zmiany hasła użytkownika
   *
   * @param user Obiekt użytkownika
   * @return token zmiany hasła
   */
  override def generateResetPasswordToken(user: User): Future[String] = userDAO.generateResetPasswordToken(user)

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego emaila.
   *
   * @param email Email poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po emailu.
   */
  override def retrieve(email: String): Future[Option[User]] = retrieve(LoginInfo(CredentialsProvider.ID, email))

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do konkretnego identyfikatora.
   *
   * @param id Identyfikator poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po identyfikatorze.
   */
  override def retrieve(id: UUID): Future[Option[User]] = userDAO.retrieve(id)

  /**
   * Odnajduje użytkownika który jest odpowiednio dopasowany do informacjami autoryzacji logowania.
   *
   * @param loginInfo Informacje autoryzacji logowania poszukiwanego użytkownika.
   * @return Odnaleziony użytkownik lub None gdy nie można odnaleźć użytkownika po informacjach dotyczących logowania.
   */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.retrieve(loginInfo)

  /**
   * Zapisuje użytkownika.
   *
   * @param user Użytkownik do zapisania.
   * @return Zapisuje użytkownika lub zwraca None gdy użytkownik istnieje.
   */
  override def save(user: User): Future[Option[User]] = userDAO.save(user)

  /**
   * Zapisuje użytkownika
   *
   * @param user dane użytkownika
   * @return left - komunikat błędu, right - dodany użytkownik
   */
  override def register(user: User)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Either[String, User]] = {
    userDAO.register(user).map {

      case Right(user) =>
        userDAO.clearResetPasswordToken(user)
        authInfoRepository.add(user.loginInfo, passwordHasherRegistry.current.hash(user.password))
        Right(user)

      case Left(error) =>
        Left(error)

    }
  }

  /**
   * Aktualizuje dane użytkownika
   *
   * @param user Użytkownik z danymi do aktualizacji.
   * @return Użytkownik z zaktualizowanymi danymi.
   */
  override def update(user: User): Future[User] = userDAO.update(user)

  /**
   * Aktualizuje hasło
   *
   * @param user     Użytkownik któremu należy zaktualizować hasło
   * @param password Hash hasła
   * @return Zaktualizowany użytkownik
   */
  override def updatePassword(user: User, password: String): Future[Option[User]] = userDAO.updatePassword(user, password)

  /**
   * Aktualizuje hasło użytkownika
   *
   * @param user     użytkownik
   * @param token    token do zmiany hasła
   * @param password nowe hasło
   * @return użytkownik jeżeli odnaleziono
   */
  override def updatePassword(user: User, token: String, password: String)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Option[User]] = {
    val result = for {
      optionalUser <- userDAO.retrieve(user.id)
    } yield for {
      user <- optionalUser.filter(_.resetPasswordToken.contains(token))
    } yield user

    result.foreach(_.foreach { user =>
      userDAO.clearResetPasswordToken(user)
      authInfoRepository.add(user.loginInfo, passwordHasherRegistry.current.hash(password))
      userDAO.generateResetPasswordToken(user)
    })

    result
  }

  /**
   * Aktualizuje hasło użytkownika
   *
   * @param userId   identyfikator użytkownika
   * @param token    token autoryzacji zmiany hasła
   * @param password nowe hasło
   * @return użytkownik jeżeli odnaleziono
   */
  override def updatePasswordWithoutLogin(userId: UUID, token: String, password: String)(implicit authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): Future[Option[User]] = {
    val result = for {
      optionalUser <- userDAO.retrieve(userId)
    } yield for {
      user <- optionalUser.filter(_.resetPasswordToken.contains(token))
    } yield for {
      _ <- userDAO.clearResetPasswordToken(user)
      _ <- userDAO.updatePassword(user, password)
      _ <- authInfoRepository.add(user.loginInfo, passwordHasherRegistry.current.hash(password))
      _ <- userDAO.generateResetPasswordToken(user)
    } yield user

    FutureUtils.flatFuture(result)
  }

}
