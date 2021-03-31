package corsign.core.jwt

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.SignedJWT

object JWTSigner {

  def signWithRSA(jwtClaims: JWTClaims, rsaKey: corsign.core.rsa.RSAKey) = {

    val signer = new RSASSASigner(rsaKey.jwkNimbusWithPrivate)

    val claimsSet = jwtClaims.toNimbus

    val signedJWT = new SignedJWT(
      new JWSHeader.Builder(JWTAlgorithm.RS512.nimbusRepresentation).keyID(rsaKey.kid).build(),
      claimsSet);

    signedJWT.sign(signer)
    signedJWT.serialize
  }

}
