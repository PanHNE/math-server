package services

import java.nio.file.Files
import java.util.UUID
import bootstrap.AppConfig
import com.sksamuel.scrimage.{Image, writer}
import com.sksamuel.scrimage.nio.JpegWriter
import daos.FileDAO

import javax.inject.Inject
import models.File
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import utils.FormHelper

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{BufferedSource, Source}

/**
 * Obsługuje wszystkie zapytania plików
 */
class FileServiceImpl @Inject()(
                                 fileDAO: FileDAO,
                                 appConfig: AppConfig
                               )(
                                 implicit
                                 context: ExecutionContext
                               ) extends FileService {

  /**
   * Usuwa plik z  bazy danych
   *
   * @param id identyfikator pliku
   */
  override def delete(id: UUID) = {
    fileDAO.delete(id).foreach { path =>
      val file = new java.io.File(s"${appConfig.filePath}$path")

      file.delete()
    }
  }

  /**
   * Zwraca plik po identyfikatorze
   *
   * @param id identyfikator pliku
   */
  override def retrieve(id: UUID): Future[Option[File]] = fileDAO.retrieve(id)

  /**
   * Zamienia plik tymczasowy na obiekt możliwy do pozyskiwania danych zawartych w pliku
   *
   * @param multipart plik tymczasowy z danymi
   * @return BufferSource lub None
   */
  override def fromTempFileToBufferSource(multipart: MultipartFormData.FilePart[TemporaryFile]): Either[String, String] = {
    if(checkingFileExtension(multipart)){
      val file = TemporaryFile.temporaryFileToFile(multipart.ref)
      val bufferSource = Source.fromFile(file)
      val source = bufferSource.getLines().mkString
      bufferSource.close()
      Right(source)
    } else {
      Left("error.bad.file.type")
    }

  }

  /**
   * Zapisuje pliki w bazie
   *
   * @param file plik do zapisania
   */
  override def save(file: FilePart[TemporaryFile], size: Int = 1): Future[Either[String, File]] = {
    makeFile(file, size) match {
      case Some(data) =>
        for {
          file <- fileDAO.save(data)
        } yield
          Right(file)

      case None =>
        Future.successful(Left("error.file.empty"))
    }
  }


  /**
   * Zapisuje w plik katalogu
   *
   * @param data dane pliku
   * @return obiekt bazodanowy pliku
   */
  private def makeFile(data: FilePart[TemporaryFile], size: Int = 1): Option[File] = if (data.filename.trim.nonEmpty) {
    val orderDirectory = LocalDate.now.format(FormHelper.dateStampFormat)
    new java.io.File(s"${appConfig.filePath}$orderDirectory").mkdirs()

    val timestamp: Long = System.currentTimeMillis
    val path = s"$orderDirectory/$timestamp.${data.filename.split('.').lastOption.getOrElse("png")}"
    val file = new java.io.File(s"${appConfig.filePath}$path")
    val thumbnail = try {
      val image = Image.fromFile(data.ref)

      image.output(file)(JpegWriter())
      size match {
        case 1 =>
          Some(image.cover(370, 200).bytes)

        case 2 =>
          Some(image.cover(568, 314).bytes)

        case _ =>
          Some(image.cover(370, 200).bytes)
      }
    } catch {
      case e: Exception =>
        Files.copy(data.ref.path, file.toPath)

        None
    }

    Some(File(UUID.randomUUID(), data.filename, data.contentType , path,  thumbnail))
  } else {
    None
  }

  /**
   * Sprawdzanie rozszerzenia pliku
   * @param multipart plik do sprawdzenia
   * @return true jeśli plik jest z rozszerzeniem xlsx lub false gdy jest inne rozszerzenie pliku
   */
  private def checkingFileExtension(multipart: MultipartFormData.FilePart[TemporaryFile]): Boolean = {
    val endNameFile = multipart.filename.split("\\.").last

    if(endNameFile == "json")
      true
    else
      false
  }
}
