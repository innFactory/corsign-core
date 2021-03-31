package corsign.core.jwk

import corsign.core.model.Payload
import play.api.libs.json.Json

case class JWK (
               p: String,
               kty: String,
               q: String,
               d: String,
               e: String,
               use: String,
               kid: String,
               qi: String,
               dp: String,
               dq: String,
               n: String
             )

object JWK {
  implicit val reads  = Json.reads[JWK]
  implicit val writes = Json.writes[JWK]
}