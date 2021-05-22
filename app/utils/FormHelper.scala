package utils

import java.time.format.DateTimeFormatter

object FormHelper {

  /**
   * Formatowanie daty
   */
  val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  /**
   * Formatowanie godziny
   */
  val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

  /**
   * Formatowanie daty
   */
  val dateStampFormat = DateTimeFormatter.ofPattern("yyyyMMdd")

}
