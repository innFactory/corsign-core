package corsign.core.model

import play.api.libs.json.Json

case class ApiResult(token: String, qrCode: String)

object ApiResult {
  implicit val reads  = Json.reads[ApiResult]
  implicit val writes = Json.writes[ApiResult]
}
