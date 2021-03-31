package corsign.core.jwk

import play.api.libs.json.Json

case class JWKS (keys: List[JWK])

object JWKS {
  implicit val reads  = Json.reads[JWKS]
  implicit val writes = Json.writes[JWKS]
}