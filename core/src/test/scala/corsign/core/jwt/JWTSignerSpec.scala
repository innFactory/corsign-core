package corsign.core.jwt

import corsign.core.app.Standalone.{ key, token }
import corsign.core.model.{ CorData, Payload, Person }
import corsign.core.model.Person.Sex.MALE
import corsign.core.rsa.RSAKey
import corsign.core.validation.SimpleRSAValidator
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.time.Instant
import java.util.{ Date, UUID }

class JWTSignerSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {
  import JWTClaims._

  val now      = Instant.now
  val validity = Instant.now.plusMillis(2.hours.toMillis)

  val person  = Person(
    firstname = "Max",
    lastname = "Mustermann",
    sex = Some(MALE),
    birthday = Some(Date.from(Instant.now())),
    phoneNumber = Some("0803199999"),
    email = Some("meine@mail.de"),
    idCardNumber = Some("LFC123ABC"),
    street1 = Some("Bahnhofstraße 1"),
    street2 = Some("c/o innFactory"),
    zip = Some("83022"),
    city = Some("Rosenheim"),
    country = Some("Germany")
  )
  val corData = CorData(isNegative = Some(true))

  "#signing" should {

    "should sign a JWTToken" in {
      val uuid = UUID.randomUUID()
      val key  = RSAKey.generateNewRSAKey(Some(uuid))

      val claims = JWTClaims(
        UUID.randomUUID(),
        "issuer",
        "audience",
        validity.getEpochSecond,
        now.getEpochSecond,
        now.getEpochSecond,
        Payload(person, corData)
      )

      val token = JWTSigner.signWithRSA(claims, key)
      token.isDefined shouldBe true
    }

    "should sign a JWTToken and validate with public key" in {
      val uuid = UUID.randomUUID()
      val key  = RSAKey.generateNewRSAKey(Some(uuid))

      val personID = UUID.randomUUID()
      val issuer   = "issuer"
      val audience = "audience"
      val exp      = validity.getEpochSecond
      val nbf      = now.getEpochSecond
      val iat      = now.getEpochSecond
      val payload  = Payload(person, corData)

      val claims = JWTClaims(
        personID,
        issuer,
        audience,
        exp,
        nbf,
        iat,
        payload
      )

      val token = JWTSigner.signWithRSA(claims, key)

      val validation = SimpleRSAValidator.validateWithRSA(token.get, key)

      validation.isDefined shouldBe true

      validation.get.payload shouldBe payload
      validation.get.sub shouldBe personID
      validation.get.iss shouldBe issuer
      validation.get.aud shouldBe audience
      validation.get.exp shouldBe exp
      validation.get.nbf shouldBe nbf
      validation.get.iat shouldBe iat
    }

    "should sign a JWTToken with RS384 and validate with public key" in {
      val uuid = UUID.randomUUID()
      val key  = RSAKey.generateNewRSAKey(Some(uuid))

      val personID = UUID.randomUUID()
      val issuer   = "issuer"
      val audience = "audience"
      val exp      = validity.getEpochSecond
      val nbf      = now.getEpochSecond
      val iat      = now.getEpochSecond
      val payload  = Payload(person, corData)

      val claims = JWTClaims(
        personID,
        issuer,
        audience,
        exp,
        nbf,
        iat,
        payload
      )

      val token      = JWTSigner.signWithRSA(claims, key, JWTAlgorithm.fromString("RS384").asInstanceOf[JwtRSAAlgorithm])
      val validation = SimpleRSAValidator.validateWithRSA(token.get, key)
      validation.isDefined shouldBe true
    }
    "should sign a JWTToken with RS256 and validate with public key" in {
      val uuid = UUID.randomUUID()
      val key  = RSAKey.generateNewRSAKey(Some(uuid))

      val personID = UUID.randomUUID()
      val issuer   = "issuer"
      val audience = "audience"
      val exp      = validity.getEpochSecond
      val nbf      = now.getEpochSecond
      val iat      = now.getEpochSecond
      val payload  = Payload(person, corData)

      val claims = JWTClaims(
        personID,
        issuer,
        audience,
        exp,
        nbf,
        iat,
        payload
      )

      val token      = JWTSigner.signWithRSA(claims, key, JWTAlgorithm.fromString("RS256").asInstanceOf[JwtRSAAlgorithm])
      val validation = SimpleRSAValidator.validateWithRSA(token.get, key)
      validation.isDefined shouldBe true
    }
  }
}
