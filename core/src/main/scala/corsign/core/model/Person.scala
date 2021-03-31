package corsign.core.model

import play.api.libs.json.Json

import java.util.{ Date, UUID }

case class Person(
  firstname: String,
  lastname: String,
  gender: Option[String] = None,
  birthday: Option[Date] = None,
  phoneNumber: Option[String] = None,
  email: Option[String] = None,
  idCardNumber: Option[String] = None,
  address: Option[String] = None,
  zip: Option[String] = None,
  city: Option[String] = None,
  country: Option[String] = None
)

object Person {
  object Gender {
    val MALE   = "M"
    val FEMALE = "F"
    val DIVERS = "D"
  }

  implicit val reads  = Json.reads[Person]
  implicit val writes = Json.writes[Person]
}
