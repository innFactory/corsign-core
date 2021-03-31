package corsign.core.validation
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.SignedJWT

import scala.util.{Success, Try}
object SimpleRSAValidator {

  def validateWithRSA(token: String, rsaKey: corsign.core.rsa.RSAKey) = {
    val rsaPublicJWK = rsaKey.nimbusJwk.toPublicJWK();
    val signedJWT = SignedJWT.parse(token)
    val verifier = new RSASSAVerifier(rsaPublicJWK)
    Try {
      if(signedJWT.verify(verifier))
        Some(signedJWT) else None
    } match {
      case Success(value) => value
      case _ => None
    }
  }
}
