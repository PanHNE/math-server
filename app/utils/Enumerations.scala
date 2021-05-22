package utils

import io.getquill.MappedEncoding

/**
 * Role użytkownika
 *
 * ADMIN - administrator systemu
 * CLIENT - klient
 * SPECIALIST - specjalista
 */
object RoleType extends Enumeration {
  type RoleType = Value

  val ADMIN = Value("ADMIN")
  val CLIENT = Value("CLIENT")
  val SPECIALIST = Value("SPECIALIST")

  implicit val encode = MappedEncoding[RoleType, String](_.toString)
  implicit val decode = MappedEncoding[String, RoleType](withName)
}

/**
 * Typ konta
 *
 * PERSON - osoba prywatna
 * COMPANY - firma
 */
object AccountType extends Enumeration {
  type AccountType = Value

  val PERSON = Value("PERSON")
  val COMPANY = Value("COMPANY")

  implicit val encode = MappedEncoding[AccountType, String](_.toString)
  implicit val decode = MappedEncoding[String, AccountType](withName)
}