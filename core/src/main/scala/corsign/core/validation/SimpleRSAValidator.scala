package corsign.core.validation
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.SignedJWT
import corsign.core.jwt.JWTClaims

import scala.util.{Failure, Success, Try}
object SimpleRSAValidator {

  def validateWithRSA(token: String, rsaKey: corsign.core.rsa.RSAKey) = {
    val rsaPublicJWK = rsaKey.nimbusJwk.toPublicJWK();
    val signedJWT = SignedJWT.parse(token)
    val verifier = new RSASSAVerifier(rsaPublicJWK)
    Try {
      if(signedJWT.verify(verifier))
        Some(JWTClaims.fromNimbus(signedJWT)) else None
    } match {
      case Success(value) => value
      case Failure(exception) => { println(exception); None }
    }
  }
}
