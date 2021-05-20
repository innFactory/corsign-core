package corsign.core.model

import play.api.libs.json.{JsValue, Json}

case class CorData(
                    isNegative: Option[Boolean] = None,
                    testType: Option[String] = None, //pcr-test  pcr-rapid-test antigen-rapid-test ag-rapid-test-w-supervision
                    testId: Option[String] = None,
                    invalid: Option[Boolean] = None,
                    isVaccinated: Option[Boolean] = None,
                    isImmune: Option[Boolean] = None,
                    vaccine: Option[String] = None,
                    appData1: Option[JsValue] = None,
                    appData2: Option[JsValue] = None,
                    creatorType: Option[String] = None,
                    carriedOutBy: Option[String] = None,
                    testManufacturer: Option[String] = None,
                    testName: Option[String] = None,
)

object CorData {
  implicit val reads  = Json.reads[CorData]
  implicit val writes = Json.writes[CorData]
}
