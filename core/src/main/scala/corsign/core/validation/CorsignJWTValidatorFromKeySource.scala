package corsign.core.validation

import com.nimbusds.jose.jwk.{JWK, JWKSelector}
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import java.util
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.jdk.CollectionConverters.SeqHasAsJava

object CorsignJWTValidatorFromKeySource {
  def apply(
             cachedJWKList: () => Future[List[JWK]], url: String, timoutDuration: Duration = 500.milliseconds
           ): CorsignJWTValidatorFromKeySource = new CorsignJWTValidatorFromKeySource(cachedJWKList, timoutDuration, url)
}

final class CorsignJWTValidatorFromKeySource(cachedJWKList: () => Future[List[JWK]], timeoutDuration: Duration, url: String) extends CorsignJWTValidatorBase {

  val issuer: String = url

  val jwkSetSource: JWKSource[SecurityContext] = new JWKSource[SecurityContext] {

    def querySource: List[JWK] = Await.result(cachedJWKList(), timeoutDuration)

    override def get(jwkSelector: JWKSelector, context: SecurityContext): util.List[JWK] = querySource.asJava
  }

}
