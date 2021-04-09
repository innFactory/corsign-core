package corsign.core.validation
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.SignedJWT
import corsign.core.jwt.JWTClaims

import scala.util.{ Failure, Success, Try }
object SimpleRSAValidator {

  def validateWithRSA(jwt: JWTToken, rsaKey: corsign.core.rsa.RSAKey) = {
    val rsaPublicJWK = rsaKey.jwkNimbus.toPublicJWK();
    val signedJWT    = SignedJWT.parse(jwt.value)
    val verifier     = new RSASSAVerifier(rsaPublicJWK)
    Try {
      if (signedJWT.verify(verifier))
        Some(JWTClaims.fromNimbus(signedJWT))
      else {
        None
      };
    } match {
      case Success(value)     => value
      case Failure(_) => None
    }
  }
}
