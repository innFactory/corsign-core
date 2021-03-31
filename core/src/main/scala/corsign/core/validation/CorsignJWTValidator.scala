package corsign.core.validation

import java.net.URL
import com.nimbusds.jose.jwk.source.{ JWKSource, RemoteJWKSet }
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import corsign.core.jwk.JWKUrl
import corsign.core.jwt.JWTClaims
import corsign.core.jwt.ProvidedValidations._

object CorsignJWTValidator {
  def apply(
    url: JWKUrl
  ): CorsignJWTValidator = new CorsignJWTValidator(url)
}

final class CorsignJWTValidator(url: JWKUrl) extends JWTValidator {

  private val issuer = url.value

  private val jwkSet: JWKSource[SecurityContext] = new RemoteJWKSet(new URL(s"${url.value}/.well-known/jwks.json"))

  private val configurableJwtValidator =
    new ConfigurableJWTValidator(
      keySource = jwkSet,
      additionalValidations = List(
        requireExpirationClaim,
        requiredIssuerClaim(issuer),
        requiredNonEmptySubject
      )
    )

  override def validate(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaimsSet)] =
    configurableJwtValidator.validate(jwtToken)

  def validateForCorsignClaims(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaims)] =
    validate(jwtToken).map(t => (t._1, JWTClaims.fromNimbusClaimSet(t._2)))
}
