package corsign.core.rsa

import com.nimbusds.jose.jwk.{KeyUse, RSAKey}
import corsign.core.jwk.JWK
import corsign.core.jwt.JWTAlgorithm.RS256
import corsign.core.jwt.JWTBase64

import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.security.{KeyPairGenerator, PrivateKey, PublicKey}
import java.util.UUID

case class RSAKey(privateKey: PrivateKey, publicKey: PublicKey, keyId: Option[UUID] = None) {
  private lazy val rsa    = publicKey.asInstanceOf[RSAPublicKey]

  lazy val n   = JWTBase64.encode(rsa.getModulus().toByteArray()).toString
  lazy val e   = JWTBase64.encode(rsa.getPublicExponent().toByteArray()).toString
  lazy val kid = keyId.getOrElse(UUID.randomUUID).toString

  lazy val jwk = JWK(rsa.getAlgorithm, kid, n, e, RS256.nimbusRepresentation.getName, KeyUse.SIGNATURE.getValue)
  lazy val nimbusJwk = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey.asInstanceOf[RSAPublicKey]).privateKey(privateKey.asInstanceOf[RSAPrivateKey]).keyUse(KeyUse.SIGNATURE).keyID(kid).build

  lazy val privateKeyPEM = s"-----BEGIN PRIVATE KEY-----\n${JWTBase64.encode(privateKey.getEncoded)}\n-----END PRIVATE KEY-----\n"
  lazy val publicKeyPEM = s"-----BEGIN PUBLIC KEY-----\n${JWTBase64.encode(publicKey.getEncoded)}\n-----END PUBLIC KEY-----\n"
}


object RSAKey {

  def generateNewRSAKey(keyId: Option[UUID]) : RSAKey = {
    val gen = KeyPairGenerator.getInstance("RSA")
    gen.initialize(2048)
    val keyPair = gen.generateKeyPair

    RSAKey(keyPair.getPrivate, keyPair.getPublic, keyId)
  }
}
