package daos

import models.File

import java.util.UUID
import scala.concurrent.Future

/**
  * Obsługuje dostęp do bazy danych obiektów plików
  */
trait FileDAO {

  /**
    * Zlicza ilosć plików w bazie
    *
    * @return ilość plików w bazie
    */
  def count: Future[Long]

  /**
   * Usuwa plik z  bazy danych
   *
   * @param id identyfikator pliku
   */
  def delete(id: UUID): Option[String]

  /**
   * Zwraca plik po identyfikatorze
   *
   * @param id identyfikator pliku
   */
  def retrieve(id: UUID): Future[Option[File]]

  /**
    * Zapisuje plik w bazie danych
    *
    * @param file obiekt pliku do zapisania
    * @return obiekt pliku
    */
  def save(file: File): Future[File]
}
