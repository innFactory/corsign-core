package corsign.core.jwt

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.SignedJWT
import corsign.core.validation.JWTToken

import scala.util.{ Success, Try }

object JWTSigner {

  def signWithRSA(
    jwtClaims: JWTClaims,
    rsaKey: corsign.core.rsa.RSAKey,
    jwtRSAAlgorithm: JwtRSAAlgorithm = JWTAlgorithm.RS512
  ): Option[JWTToken] = {

    val signer = new RSASSASigner(rsaKey.jwkNimbusWithPrivate)

    val claimsSet = jwtClaims.toNimbus

    val signedJWT =
      new SignedJWT(new JWSHeader.Builder(jwtRSAAlgorithm.nimbusRepresentation).keyID(rsaKey.kid).build(), claimsSet);

    Try {
      signedJWT.sign(signer)
      JWTToken(signedJWT.serialize)
    } match {
      case Success(value) => Some(value)
      case _              => None
    }
  }

}
