package corsign.core.rsa

import com.nimbusds.jose.jwk.{ JWK, KeyUse }
import com.roundeights.hasher.Implicits._
import corsign.core.jwt.JWTAlgorithm
import org.bouncycastle.util.io.pem.{ PemObject, PemWriter }
import play.api.libs.json.{ JsValue, Json }
import java.io.StringWriter
import java.security.interfaces.{ RSAPrivateKey, RSAPublicKey }
import java.security.{ KeyPairGenerator, PrivateKey, PublicKey }
import java.util.UUID
import scala.language.postfixOps

case class RSAKey(privateKey: PrivateKey, publicKey: PublicKey, _keyId: Option[UUID] = None) {

  lazy val kid = _keyId.getOrElse(UUID.randomUUID).toString

  lazy val jwkNimbusWithPrivate = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey.asInstanceOf[RSAPublicKey])
    .privateKey(privateKey.asInstanceOf[RSAPrivateKey])
    .keyUse(KeyUse.SIGNATURE)
    .algorithm(JWTAlgorithm.RS512.nimbusRepresentation)
    .keyID(kid)
    .build
  lazy val jwkNimbus            = jwkNimbusWithPrivate.toPublicJWK
  lazy val jwkJson: JsValue     = Json.parse(jwkNimbus.toJSONString)

  lazy val privateKeyPEM    = keyToPem(privateKey.getEncoded, "PRIVATE")
  lazy val privateKeySHA512 = privateKey.getEncoded.sha512.hash.toString()
  lazy val publicKeyPEM     = keyToPem(publicKey.getEncoded)

  private def keyToPem(key: Array[Byte], tpe: String = "PUBLIC") = {
    val output    = new StringWriter
    val pemWriter = new PemWriter(output)
    val pem       = new PemObject(s"$tpe KEY", key)
    pemWriter.writeObject(pem)
    pemWriter.close()
    output.toString
  }
}

object RSAKey {

  def generateNewRSAKey(keyId: Option[UUID] = None): RSAKey = {
    val gen     = KeyPairGenerator.getInstance("RSA")
    gen.initialize(2048)
    val keyPair = gen.generateKeyPair
    RSAKey(keyPair.getPrivate, keyPair.getPublic, keyId)
  }

  def fromPEM(pem: String, keyId: Option[UUID] = None): RSAKey = {
    val jwk        = JWK.parseFromPEMEncodedObjects(pem)
    val privateKey = jwk.toRSAKey.toPrivateKey
    val publicKey  = jwk.toRSAKey.toPublicKey
    RSAKey(privateKey, publicKey, keyId)
  }

}
