package corsign.core.app
// $COVERAGE-OFF$
import corsign.core.jwt.{JWTClaims, JWTSigner}
import corsign.core.model.Person.Sex.MALE
import corsign.core.model.{CorData, Payload, Person}
import corsign.core.qr.{B64QRCode, QRData}
import corsign.core.rsa.RSAKey
import corsign.core.validation.SimpleRSAValidator

import java.time.{Instant, LocalDate}
import java.util.{Date, UUID}
import scala.concurrent.duration.DurationInt

object Standalone extends App {

  val uuid = UUID.randomUUID()
  println(s"Generating a new RSA key. $uuid")
  val key  = RSAKey.generateNewRSAKey(Some(uuid))
  val key2  = RSAKey.generateNewRSAKey(Some(uuid))

  println("Private Key:")
  println(key.privateKeyPEM)
  println("Public Key:")
  println(key.publicKeyPEM)


  val now      = Instant.now
  val validity = Instant.now.plusMillis(1000.days.toMillis)

  val person  = Person(
    firstname = "Max",
    lastname = "Mustermann",
    sex = Some(MALE),
    birthday = LocalDate.of(1999,2,3),
    phoneNumber = Some("0803199999"),
    email = Some("max@mustermann.de"),
    idCardNumber = Some("LFC123ABC"),
    street1 = Some("Eduard-Rüber-Str. 7"),
    street2 = Some("c/o innFactory GmbH"),
    zip = Some("83022"),
    city = Some("Rosenheim"),
    country = Some("Germany")
  )
  val corData = CorData(isNegative = Some(true))

  val claims = JWTClaims(
    UUID.randomUUID(),
    "https://iss.corsign.de/",
    "test",
    validity.getEpochSecond,
    now.getEpochSecond,
    now.getEpochSecond,
    Payload(person, corData)
  )

  val token = JWTSigner.signWithRSA(claims, key)
  println("\nSigned Token with this Key is:")
  println(token)
  println("\nParsed content from validated Token is:")
  println(SimpleRSAValidator.validateWithRSA(token.get, key).get)

  println(key.jwkJson.toString())

  println("\nNow try to sign with a Deserialized PEM Key:")
  val token2 = JWTSigner.signWithRSA(claims, RSAKey.fromPEM(key.privateKeyPEM))
  println("\nThe second JWT Token is:")
  println(token2)

  println("\nInvalid JWT validation (wrong key):")
  println(SimpleRSAValidator.validateWithRSA(token2.get, key2))

  println("\nPrivate Key Hash for signing endpoint:")
  println(key.privateKeySHA512)


  println("\n\nNow generating a QR Code")

  val url = s"https://corsign.de/v1/validate/${token.get.value}"
  println(B64QRCode().generate(QRData(url)))
}
// $COVERAGE-ON$
