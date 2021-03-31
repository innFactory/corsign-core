package corsign.core.model

import play.api.libs.json.Json

case class CorData(
    isNegative: Option[Boolean] = None,
    isVaccinated: Option[Boolean] = None,
    vaccine: Option[String] = None
)

object CorData {
  implicit val reads  = Json.reads[CorData]
  implicit val writes = Json.writes[CorData]
}