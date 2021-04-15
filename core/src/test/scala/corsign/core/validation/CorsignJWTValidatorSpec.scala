package corsign.core.validation

import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import corsign.core.jwk.JWKUrl
import corsign.core.rsa.RSAKey
import corsign.core.validation.Generators.jwkSourceGen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.security.{KeyPair, KeyPairGenerator}
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import corsign.core.validation.utils.RightMap._
import play.api.libs.json.Json

class CorsignJWTValidatorSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  "#validate" should {
    "should create a custom" in {
      val correctlyConfiguredValidator = CorsignJWTValidator(JWKUrl("http://localhost"))
    }

    "should create a jwtValidatorFromKeySource" in {
      val rsaKey: RSAKey      = RSAKey.generateNewRSAKey()

      var futureList: Future[List[JWK]] = Future(List.empty[JWK]) // Start with empty Key "Source"

      val correctlyConfiguredValidator = CorsignJWTValidatorFromKeySource(() => futureList, "https://openid.c2id.com")

    }

    "should validate with jwtValidatorFromKeySource" in {
      val rsaKey: RSAKey      = RSAKey.generateNewRSAKey()

      var futureList: Future[List[JWK]] = Future(List.empty[JWK]) // Start with empty Key "Source"

      val correctlyConfiguredValidator = CorsignJWTValidatorFromKeySource(() => futureList, "https://openid.c2id.com")

      val tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS))

      val claims = new JWTClaimsSet.Builder()
        .issuer("https://openid.c2id.com")
        .subject("alice")
        .expirationTime(tomorrow)
        .build

      val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claims)
      jwt.sign(new RSASSASigner(rsaKey.privateKey))
      val token  = JWTToken(value = jwt.serialize())

      val failed = correctlyConfiguredValidator.validate(token) // Should fail, because there a no keys
      failed.isLeft shouldBe true

      futureList = Future(List(JWK.parse(Json.prettyPrint(rsaKey.jwkJson)))) // Add Key to Key "Source"

      val res = correctlyConfiguredValidator.validate(token) // Should succeed, because valid key was added
      res.rightMap(_._1) shouldBe Right(token)
      res.rightMap(_._2).toString shouldBe Right(claims).toString

    }
  }
}
