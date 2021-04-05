package corsign.core.model

import play.api.libs.json.{JsValue, Json}

case class CorData(
                    isNegative: Option[Boolean] = None,
                    testType: Option[String] = None,
                    isVaccinated: Option[Boolean] = None,
                    vaccine: Option[String] = None,
                    appData1: Option[JsValue] = None,
                    appData2: Option[JsValue] = None
)

object CorData {
  implicit val reads  = Json.reads[CorData]
  implicit val writes = Json.writes[CorData]
}
