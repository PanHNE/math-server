package models

import io.getquill.Embedded

import java.util.UUID

/**
  * Plik
  *
  * @param id           unikalny identyfikator
  * @param contentType  rodzaj zawartości (mime)
  * @param path         ścieżka do pliku
  * @param filename     nazwa pliku z rozszerzeniem
 * @param thumbnail   miniaturka pliku
  */
case class File(
  id: UUID,
  filename: String,
  contentType: Option[String],
  path: String,
  thumbnail: Option[Array[Byte]] = None
) extends Embedded
