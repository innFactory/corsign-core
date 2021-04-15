package corsign.core.validation

import java.net.URL
import com.nimbusds.jose.jwk.source.{JWKSource, RemoteJWKSet}
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import corsign.core.jwk.JWKUrl
import corsign.core.validation.CorsignJWTValidator.DEFAULT_HTTP_SIZE_LIMIT

object CorsignJWTValidator {
  val DEFAULT_HTTP_SIZE_LIMIT = 25 * 1024 * 1024
  def apply(
    url: JWKUrl
  ): CorsignJWTValidator = new CorsignJWTValidator(url)
}

final class CorsignJWTValidator(url: JWKUrl) extends CorsignJWTValidatorBase {

  val issuer = url.value

  val jwkSetSource: JWKSource[SecurityContext] = new RemoteJWKSet(new URL(s"${url.value}/.well-known/jwks.json"), new DefaultResourceRetriever(4000, 4000, DEFAULT_HTTP_SIZE_LIMIT))

}

