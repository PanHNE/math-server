package utils

/**
 * Zaimplementuj to by dostać nazwę logger zakresu.
 */
trait Logger {

  /**
   * Nazwa instancji logger.
   */
  val logger = play.api.Logger(this.getClass)
}
