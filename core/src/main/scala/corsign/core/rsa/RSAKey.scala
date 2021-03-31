package corsign.core.rsa

import com.nimbusds.jose.jwk.{KeyUse, RSAKey}
import corsign.core.app.Standalone.key
import corsign.core.jwk.JWK
import corsign.core.jwt.JWTAlgorithm.RS256
import corsign.core.jwt.JWTBase64
import play.api.libs.json.Json

import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.security.{KeyPairGenerator, PrivateKey, PublicKey}
import java.util.UUID

case class RSAKey(privateKey: PrivateKey, publicKey: PublicKey, _keyId: Option[UUID] = None) {
  private lazy val rsaPublicKey    = publicKey.asInstanceOf[RSAPublicKey]

  lazy val kid = _keyId.getOrElse(UUID.randomUUID).toString

  lazy val nimbusJwk = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey.asInstanceOf[RSAPublicKey]).privateKey(privateKey.asInstanceOf[RSAPrivateKey]).keyUse(KeyUse.SIGNATURE).keyID(kid).build
  lazy val jwkJsonString = nimbusJwk.toJSONString
  lazy val jwk: JWK = Json.parse(nimbusJwk.toJSONString).as[JWK]

  lazy val privateKeyPEM = s"-----BEGIN PRIVATE KEY-----\n${JWTBase64.encodeString(privateKey.getEncoded)}\n-----END PRIVATE KEY-----\n"
  lazy val publicKeyPEM = s"-----BEGIN PUBLIC KEY-----\n${JWTBase64.encodeString(publicKey.getEncoded)}\n-----END PUBLIC KEY-----\n"


}


object RSAKey {

  def generateNewRSAKey(keyId: Option[UUID] = None) : RSAKey = {
    val gen = KeyPairGenerator.getInstance("RSA")
    gen.initialize(2048)
    val keyPair = gen.generateKeyPair

    RSAKey(keyPair.getPrivate, keyPair.getPublic, keyId)
  }
}
