package services

import models.File
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart

import java.util.UUID
import scala.concurrent.Future

/**
  * Obsługuje wszystkie zapytania plików
  */
trait FileService {

  /**
   * Usuwa plik z  bazy danych
   *
   * @param id identyfikator pliku
   */
  def delete(id: UUID)

  /**
    * Zamienia plik tymczasowy na obiekt możliwy do pozyskiwania danych zawartych w pliku
    *
    * @param multipart plik tymczasowy z danymi
    * @return BufferSource lub None
    */
  def fromTempFileToBufferSource(multipart: MultipartFormData.FilePart[TemporaryFile]): Either[String, String]

  /**
    * Zwraca plik po identyfikatorze
    *
    * @param id identyfikator pliku
    */
  def retrieve(id: UUID): Future[Option[File]]

  /**
    * Zapisuje plik w bazie
    *
    * @param file plik do zapisania
    * @return obiekt bazodanowy pliku
    */
  def save(file: FilePart[TemporaryFile], size: Int = 1): Future[Either[String, File]]
}
