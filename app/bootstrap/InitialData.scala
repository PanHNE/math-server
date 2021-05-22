package bootstrap

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.Logger
import play.api.db.evolutions.ApplicationEvolutions
import services.UserService
import utils.RoleType

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

/**
 * Dane inicjalizacyjne aplikacji
 */
@Singleton
private[bootstrap] class InitialData @Inject()(
                                                userService: UserService,
                                                authInfoRepository: AuthInfoRepository,
                                                passwordHasherRegistry: PasswordHasherRegistry,
                                                applicationEvolutions: ApplicationEvolutions
                                              )(implicit context: ExecutionContext) {
  val logger = Logger("application")

  /**
   * Inicjalizuje bazę danych domyślnymi wartościami
   */
  def insertInitialData() = if (applicationEvolutions.upToDate) {
    userService.count.map { userCount =>
      if (userCount == 0) {
        logger.info("InitialData: Created admin user.")

        InitialData.users.foreach { userData =>
          for {
            user <- userService.save(userData)
            _ <- authInfoRepository.add(userData.loginInfo, passwordHasherRegistry.current.hash(userData.password))
          } yield {
            user
          }
        }
      }
    }
  }

  insertInitialData()
}

/**
 * Dane początkowe aplikacji
 */
private[bootstrap] object InitialData {

  /**
   * Pierwszy użytkownik z rolą w systemie ADMIN, służy do sprawdzania działania aplikacji,
   * jak również zarządzania nią.
   * Przy wrzucaniu aplikacji na produkcję w końcowym etapie może zostać usunięty.
   */
  def users = List(
    User(UUID.randomUUID(), CredentialsProvider.ID, RoleType.ADMIN, "admin@math.pl", "qwertyuiop",
      Some("Adrian"), Some("Domin"), Some("787512006"))
  )
}
