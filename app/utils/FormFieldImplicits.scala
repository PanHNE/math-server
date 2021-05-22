package utils

import play.api.data.format.Formatter
import play.api.data.{FormError, Forms, Mapping}

object FormFieldImplicits {

  /**
   * Formater typu Double
   */
  private def doubleFormat: Formatter[Double] = new Formatter[Double] {
    def bind(key: String, data: Map[String, String]) =
      scala.util.control.Exception.allCatch[Double]
        .either(data(key).toDouble)
        .left.map(e => Seq(FormError(key, "error.double")))
    def unbind(key: String, value: Double) = Map(key -> value.toString)
  }

  def doubleNumber: Mapping[Double] = Forms.of(doubleFormat)

  /**
   * Formater typu Double
   */
  private def telephoneFormat: Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[String]
          .either(s.replaceAll("[^\\d]", ""))
          .left.map(e => Seq(FormError(key, "error.telephone")))
      }
    }
    def unbind(key: String, value: String) = Map(key -> value)
  }

  def telephone: Mapping[String] = Forms.of(telephoneFormat)

  /**
   * Formater typu Enum
   */
  private def enumFormat[E <: Enumeration#Value](enum: Enumeration): Formatter[E] = new Formatter[E] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E]
          .either(enum.withName(s).asInstanceOf[E])
          .left.map(e => Seq(FormError(key, "error.enum")))
      }
    }
    def unbind(key: String, value: E) = Map(key -> value.toString)
  }

  def enum[F <: Enumeration#Value](enum: Enumeration): Mapping[F]  = Forms.of(enumFormat(enum))
}
