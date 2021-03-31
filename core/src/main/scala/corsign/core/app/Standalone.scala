package corsign.core.app
// $COVERAGE-OFF$
import corsign.core.jwt.{JWTClaims, JWTSigner}
import corsign.core.model.Person.Gender.MALE
import corsign.core.model.{CorData, Payload, Person}
import corsign.core.qr.{B64QRCode, QRData}
import corsign.core.rsa.RSAKey
import corsign.core.validation.SimpleRSAValidator

import java.time.Instant
import java.util.{Date, UUID}
import scala.concurrent.duration.DurationInt


object Standalone extends App {

  val uuid = UUID.randomUUID()
  println(s"Generating a new RSA key. $uuid")
  val key = RSAKey.generateNewRSAKey(Some(uuid))

  println("Public Key:")
  println(key.publicKeyPEM)


  val now          = Instant.now
  val validity     = Instant.now.plusMillis(2.hours.toMillis)

  val person = Person(
    firstname = "Max",
    lastname = "Mustermann",
    gender = Some(MALE),
    birthday = Some(Date.from(Instant.now())),
    phoneNumber = Some("0803199999"),
    email = Some("meine@mail.de"),
    idCardNumber = Some("LFC123ABC"),
    address = Some("Bahnhofstraße 1"),
    zip = Some("83022"),
    city = Some("Rosenheim"),
    country = Some("Germany"),
  )
  val corData = CorData(isNegative = Some(true))

  val claims = JWTClaims(
    UUID.randomUUID(),
    "issuer",
    "audience",
    validity.getEpochSecond,
    now.getEpochSecond,
    now.getEpochSecond,
    Payload(person, corData),
  )

  val token = JWTSigner.signWithRSA(claims, key)
  println("\nSigned Token with this Key is:")
  println(token)
  println("\nParsed Content from validated Token is")
  println(SimpleRSAValidator.validateWithRSA(token.get, key).get)

  println(key.jwkJson.toString())

  println("\nNow try to sign with a Deserialized PEM Key")
  val token2 = JWTSigner.signWithRSA(claims, RSAKey.fromPEM(key.privateKeyPEM))
  println("\nSecond JWT Token is:")
  println(token2)

  println("\nPrivate Key Hash for Signing Endpoint")
  println(key.privateKeySHA512)


  println("\n\nNow generating a QR Code")

  val url = s"http://localhost:5000/v1/validate/${token.get.value}"
  println(B64QRCode().generate(QRData(url)))
}
// $COVERAGE-ON$