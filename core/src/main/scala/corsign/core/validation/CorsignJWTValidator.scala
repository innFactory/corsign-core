package corsign.core.validation

import java.net.URL
import com.nimbusds.jose.jwk.source.{DefaultJWKSetCache, JWKSource, RemoteJWKSet}
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import corsign.core.jwk.JWKUrl
import corsign.core.jwt.{JWTAlgorithm, JWTClaims}
import corsign.core.jwt.ProvidedValidations._
import corsign.core.validation.CorsignJWTValidator.DEFAULT_HTTP_SIZE_LIMIT

object CorsignJWTValidator {
  val DEFAULT_HTTP_SIZE_LIMIT = 25 * 1024 * 1024
  def apply(
    url: JWKUrl
  ): CorsignJWTValidator = new CorsignJWTValidator(url)
}

final class CorsignJWTValidator(url: JWKUrl) extends JWTValidator {

  private val issuer = url.value

  private val jwkSet: JWKSource[SecurityContext] = new RemoteJWKSet(new URL(s"${url.value}/.well-known/jwks.json"), new DefaultResourceRetriever(4000, 4000, DEFAULT_HTTP_SIZE_LIMIT))

  private val configurableJwtValidator =
    new ConfigurableJWTValidator(
      keySource = jwkSet,
      additionalValidations = List(
        requireExpirationClaim,
        requiredIssuerClaim(issuer),
        requiredNonEmptySubject
      ),
      algorithm = JWTAlgorithm.RS512
    )

  override def validate(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaimsSet)] =
    configurableJwtValidator.validate(jwtToken)

  def validateForCorsignClaims(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaims)] =
    validate(jwtToken).map(t => (t._1, JWTClaims.fromNimbusClaimSet(t._2)))
}
