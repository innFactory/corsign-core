package corsign.core.jwk

import play.api.libs.json.{JsValue, Json}

case class JWKS (keys: List[JsValue])

object JWKS {
  implicit val reads  = Json.reads[JWKS]
  implicit val writes = Json.writes[JWKS]
}