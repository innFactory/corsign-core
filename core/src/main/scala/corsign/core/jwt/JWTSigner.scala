package corsign.core.jwt

import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.{Date, UUID}

object JWTSigner {

  def signWithRSA(jwtClaims: JWTClaims, rsaKey: corsign.core.rsa.RSAKey) = {

    val signer = new RSASSASigner(rsaKey.nimbusJwk)

    val claimsSet = new JWTClaimsSet.Builder()
      .issuer(jwtClaims.iss)
      .expirationTime(new Date(jwtClaims.exp))
      .notBeforeTime(new Date(jwtClaims.nbf))
      .issueTime(new Date(jwtClaims.iat))
      .audience(jwtClaims.aud)
      .claim("sub", jwtClaims.sub)
      .claim("claims", jwtClaims.corData)
      .build();

    val signedJWT = new SignedJWT(
      new JWSHeader.Builder(JWTAlgorithm.RS256.nimbusRepresentation).keyID(rsaKey.kid).build(),
      claimsSet);

    signedJWT.sign(signer)
    signedJWT.serialize
  }

}
