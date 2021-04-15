package corsign.core.validation

import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.{ JWSAlgorithm, JWSHeader }
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.{ JWTClaimsSet, SignedJWT }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import corsign.core.validation.utils.RightMap._
import java.security.{ KeyPair, KeyPairGenerator }
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Some parts of these tests code is inspired and/or copy/paste from Nimbus tests code. The Base of this Validator Spec comes from an Open Source implementation of JWK Validation.
 */
class ConfigurableJWTValidatorSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  import Generators._
  import corsign.core.jwt.ProvidedValidations._

  "true" should { "be true" in { true shouldNot be(false) } }

  "#validate" should {
    val gen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    gen.initialize(2048)
    val keyPair: KeyPair      = gen.generateKeyPair()

    "when the JSON Web Token is an empty String" should {
      "returns Left(EmptyJwtTokenContent)" in {
        forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
          val token     = JWTToken(value = "")
          val validator = ConfigurableJWTValidator(jwkSource)
          validator.validate(token) shouldBe Left(EmptyJwtTokenContent)
        }
      }
    }

    "when the JWT is invalid" should {
      "returns Left(InvalidJwtToken)" in {
        forAll(jwkSourceGen(keyPair), nonEmptyStringGen) {
          (jwkSource: JWKSource[SecurityContext], randomString: String) =>
            val token     = JWTToken(value = randomString)
            val validator = ConfigurableJWTValidator(jwkSource)
            validator.validate(token) shouldBe Left(InvalidJwtToken)
        }
      }
    }

    "when the `exp` claim" should {
      "is not required but present" should {
        "but expired" should {
          "returns Left(BadJWTException: Expired JWT)" in {
            val yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS))

            val claims = new JWTClaimsSet.Builder()
              .issuer("https://openid.c2id.com")
              .subject("alice")
              .expirationTime(yesterday)
              .build
            val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
            jwt.sign(new RSASSASigner(keyPair.getPrivate))
            val token  = JWTToken(value = jwt.serialize())

            forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
              val res = ConfigurableJWTValidator(jwkSource).validate(token)
              res.toString shouldBe Left(new BadJWTException("Expired JWT")).toString
            }
          }
        }
        "and valid" should {
          "returns Right(token -> claimSet)" in {
            val tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS))

            val claims = new JWTClaimsSet.Builder()
              .issuer("https://openid.c2id.com")
              .subject("alice")
              .expirationTime(tomorrow)
              .build
            val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
            jwt.sign(new RSASSASigner(keyPair.getPrivate))
            val token  = JWTToken(value = jwt.serialize())

            forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
              val res = ConfigurableJWTValidator(jwkSource).validate(token)
              res.rightMap(_._1) shouldBe Right(token)
              res.rightMap(_._2).toString shouldBe Right(claims).toString
            }
          }
        }
      }
      "is required" should {
        "but not present" should {
          "returns Left(MissingExpirationClaim)" in {
            val claims = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").subject("alice").build
            val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
            jwt.sign(new RSASSASigner(keyPair.getPrivate))
            val token  = JWTToken(value = jwt.serialize())

            forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
              val correctlyConfiguredValidator =
                ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireExpirationClaim))
              val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

              correctlyConfiguredValidator.validate(token) shouldBe Left(MissingExpirationClaim)
              val res = nonConfiguredValidator.validate(token)
              res.rightMap(_._1) shouldBe Right(token)
              // Without the `.toString` hack, we have this stupid error:
              //  `Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"}) was not equal to Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"})`
              // Equality on Claims should not be well defined.
              res.rightMap(_._2).toString shouldBe Right(claims).toString
            }
          }
        }
        "and present" should {
          "but expired" should {
            "returns Left(BadJWTException: Expired JWT)" in {
              val yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS))

              val claims = new JWTClaimsSet.Builder()
                .issuer("https://openid.c2id.com")
                .subject("alice")
                .expirationTime(yesterday)
                .build
              val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
              jwt.sign(new RSASSASigner(keyPair.getPrivate))
              val token  = JWTToken(value = jwt.serialize())

              forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
                val correctlyConfiguredValidator =
                  ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireExpirationClaim))

                val res = correctlyConfiguredValidator.validate(token)
                res.toString shouldBe Left(new BadJWTException("Expired JWT")).toString
              }
            }
          }
          "and valide" should {
            "returns Right(token -> claimSet)" in {
              val tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS))

              val claims = new JWTClaimsSet.Builder()
                .issuer("https://openid.c2id.com")
                .subject("alice")
                .expirationTime(tomorrow)
                .build
              val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
              jwt.sign(new RSASSASigner(keyPair.getPrivate))
              val token  = JWTToken(value = jwt.serialize())

              forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
                val correctlyConfiguredValidator =
                  ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireExpirationClaim))

                val res = correctlyConfiguredValidator.validate(token)
                res.rightMap(_._1) shouldBe Right(token)
                res.rightMap(_._2).toString shouldBe Right(claims).toString
              }
            }
          }
        }
      }
    }

    "when the `use` claim is required" should {
      "but not present" should {
        "returns Left(InvalidTokenUseClaim)" in {
          val tokenUse = "some random string"
          val claims   = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").subject("alice").build
          val jwt      = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token    = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireTokenUseClaim(tokenUse)))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenUseClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "present but not the one expected" should {
        "returns Left(InvalidTokenUseClaim)" in {
          val tokenUse = "some random string"
          val claims   = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").subject("alice").build
          val jwt      = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token    = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireTokenUseClaim(tokenUse + "s")))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenUseClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "present and valid" should {
        "returns Right(token -> claimSet)" in {
          val tokenUse = "some random string"
          val claims   = new JWTClaimsSet.Builder()
            .issuer("https://openid.c2id.com")
            .subject("alice")
            .claim("token_use", tokenUse)
            .build
          val jwt      = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token    = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireTokenUseClaim(tokenUse)))
            val res                          = correctlyConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
    }

    "when the `iss` claim is required " should {
      "but not present" should {
        "returns Left(InvalidTokenIssuerClaim)" in {
          val issuer = "https://openid.c2id.com"
          val claims = new JWTClaimsSet.Builder().subject("alice").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredIssuerClaim(issuer)))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenIssuerClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "and present but not the one expected" should {
        "returns Left(InvalidTokenIssuerClaim)" in {
          val issuer = "https://guizmaii.com"
          val claims = new JWTClaimsSet.Builder().issuer(issuer).subject("alice").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredIssuerClaim(issuer + "T")))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenIssuerClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "present and valide" should {
        "returns Right(token -> claimSet)" in {
          val issuer = "https://guizmaii.com"
          val claims = new JWTClaimsSet.Builder().issuer(issuer).subject("alice").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val res = ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredIssuerClaim(issuer)))
              .validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
    }

    "when the `sub` claim is required" should {
      "when not present" should {
        "returns Left(InvalidTokenSubject)" in {
          val claims = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredNonEmptySubject))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenSubject)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            // Without the `.toString` hack, we have this stupid error:
            //  `Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"}) was not equal to Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"})`
            // Equality on Claims should not be well defined.
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "when present but empty" should {
        "returns Left(InvalidTokenSubject)" in {
          val claims = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").subject("").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredNonEmptySubject))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidTokenSubject)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "when present and valid" should {
        "returns Right(token -> claimSet)" in {
          val claims = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").subject("Jules").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val res =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requiredNonEmptySubject)).validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
    }

    "when the `aud` claim is required" should {
      "when not present" should {
        "returns Left(InvalidAudienceClaim)" in {
          val claims = new JWTClaimsSet.Builder().issuer("https://openid.c2id.com").build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(
                jwkSource,
                additionalValidations = List(requireAudience("https://valid_audience.com"))
              )
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidAudienceClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            // Without the `.toString` hack, we have this stupid error:
            //  `Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"}) was not equal to Right({"sub":"alice","iss":"https:\/\/openid.c2id.com"})`
            // Equality on Claims should not be well defined.
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "when present and invalid" should {
        "returns Left(InvalidAudienceClaim)" in {
          val claims =
            new JWTClaimsSet.Builder()
              .issuer("https://openid.c2id.com")
              .audience("valid_audience_1")
              .audience("valid_audience_2")
              .build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val correctlyConfiguredValidator =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireAudience("invalid_audience")))
            val nonConfiguredValidator       = ConfigurableJWTValidator(jwkSource)

            correctlyConfiguredValidator.validate(token) shouldBe Left(InvalidAudienceClaim)
            val res = nonConfiguredValidator.validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
      "when present and valid" should {
        "returns Right(token -> claimSet)" in {
          val claims =
            new JWTClaimsSet.Builder()
              .issuer("https://openid.c2id.com")
              .audience("valid_audience_1")
              .audience("valid_audience_2")
              .build
          val jwt    = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims)
          jwt.sign(new RSASSASigner(keyPair.getPrivate))
          val token  = JWTToken(value = jwt.serialize())

          forAll(jwkSourceGen(keyPair)) { jwkSource: JWKSource[SecurityContext] =>
            val res =
              ConfigurableJWTValidator(jwkSource, additionalValidations = List(requireAudience("valid_audience_2")))
                .validate(token)
            res.rightMap(_._1) shouldBe Right(token)
            res.rightMap(_._2).toString shouldBe Right(claims).toString
          }
        }
      }
    }
  }


}
