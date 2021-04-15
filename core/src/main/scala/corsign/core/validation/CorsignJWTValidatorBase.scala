package corsign.core.validation

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import corsign.core.jwt.{JWTAlgorithm, JWTClaims}
import corsign.core.jwt.ProvidedValidations.{requireExpirationClaim, requiredIssuerClaim, requiredNonEmptySubject}

abstract class CorsignJWTValidatorBase extends JWTValidator {

    val issuer: String

    val jwkSetSource: JWKSource[SecurityContext]

    private lazy val configurableJwtValidator =
      new ConfigurableJWTValidator(
        keySource = jwkSetSource,
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
