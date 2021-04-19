package corsign.core.model

import play.api.libs.json.Json

import java.time.LocalDate
import java.util.{Date, UUID}

case class Person(
                   firstname: String,
                   lastname: String,
                   sex: Option[String] = None,
                   birthday: Option[LocalDate] = None,
                   phoneNumber: Option[String] = None,
                   email: Option[String] = None,
                   idCardNumber: Option[String] = None,
                   street1: Option[String] = None,
                   street2: Option[String] = None,
                   zip: Option[String] = None,
                   city: Option[String] = None,
                   country: Option[String] = None
)

object Person {
  object Sex {
    val MALE   = "M"
    val FEMALE = "F"
    val DIVERS = "D"
  }

  implicit val reads  = Json.reads[Person]
  implicit val writes = Json.writes[Person]
}
