package corsign.core.validation

import java.net.URL
import com.nimbusds.jose.jwk.source.{JWKSource, RemoteJWKSet}
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import corsign.core.jwk.JWKUrl
import corsign.core.jwt.ProvidedValidations._


object CorsignJwtValidator {
  def apply(
             url: JWKUrl
           ): CorsignJwtValidator = new CorsignJwtValidator(url)
}

final class CorsignJwtValidator(url: JWKUrl) extends JwtValidator {

  private val issuer = url.value

  private val jwkSet: JWKSource[SecurityContext] = new RemoteJWKSet(new URL(s"${url.value}/.well-known/jwks.json"))

  private val configurableJwtValidator =
    new ConfigurableJwtValidator(
      keySource = jwkSet,
      additionalValidations = List(
        requireExpirationClaim,
        requiredIssuerClaim(issuer),
        requiredNonEmptySubject,
      )
    )

  override def validate(jwtToken: JwtToken): Either[BadJWTException, (JwtToken, JWTClaimsSet)] =
    configurableJwtValidator.validate(jwtToken)
}