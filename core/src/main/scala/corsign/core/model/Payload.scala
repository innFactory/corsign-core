package corsign.core.model

import play.api.libs.json.Json

case class Payload(
                    person: Person,
                    information: CorData)
  extends JsonSerializeable {
    def toJson = Json.toJson(this)(Payload.writes)
}

object Payload {
    implicit val reads  = Json.reads[Payload]
    implicit val writes = Json.writes[Payload]
}
