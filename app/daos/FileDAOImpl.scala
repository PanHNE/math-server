package daos

import database.{DatabaseConnector, QueryExtension}
import models.File

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileDAOImpl @Inject() (val database: DatabaseConnector) (implicit context: ExecutionContext)
  extends FileDAO with QueryExtension {

  import database.ctx._

  private val files = quote(querySchema[File]("files"))

  /**
    * Zlicza ilosć plików w bazie
    *
    * @return ilość plików w bazie
    */
  override def count: Future[Long] = Future.successful(run(files.size))

  /**
   * Usuwa plik z bazy danych
   *
   * @param id identyfikator pliku
   * @return ścieżka do plikuy na dysku
   */
  override def delete(id: UUID): Option[String] = transaction {
    val fileQuery = quote(files.filter(_.id == lift(id)))
    val path = run(fileQuery).headOption.map(_.path)

    run(fileQuery.delete)
    path
  }

  /**
   * Wyszukuje pliku na podstawie identyfikatora
   *
   * @param id identyfikator
   * @return obiekt pliku, jeżeli został odnaleziony
   */
  override def retrieve(id: UUID): Future[Option[File]] = Future.successful(
    run(files.filter(_.id == lift(id))).headOption
  )

  /**
    * Zapisuje plik w bazie danych
    *
    * @param file obiekt pliku do zapisania
    * @return obiekt pliku
    */
  override def save(file: File): Future[File] = {
    run(files.insert(lift(file)))
    Future.successful(file)
  }
}
