package corsign.core.model

import play.api.libs.json.Json

case class Payload(person: Person, information: CorData)

object Payload {
  implicit val reads  = Json.reads[Payload]
  implicit val writes = Json.writes[Payload]
}
