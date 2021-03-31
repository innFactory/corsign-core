package corsign.core.jwk

import corsign.core.app.Standalone.uuid
import corsign.core.rsa.RSAKey
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import java.util.UUID

class JWKSpec extends AnyWordSpec with Matchers {
  "#jwks" should {
    "be serialized to a json string with starts with a list of keys" in {
      val uuid = UUID.randomUUID()
      val key = RSAKey.generateNewRSAKey(Some(uuid))
      val key2 = RSAKey.generateNewRSAKey(Some(uuid))

      val jwks = JWKS(List(key.jwkJson, key2.jwkJson))
      Json.toJson(jwks).toString.startsWith("{\"keys\":[{\"kty\":\"RSA\",") shouldBe true
    }

    "set an url" in {
      val jwkUrl = JWKUrl("set")
      jwkUrl.value shouldBe "set"
    }
  }
}
