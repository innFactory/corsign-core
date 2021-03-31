package corsign.core.app

import corsign.core.jwt.{JWTClaims, JWTSigner}
import corsign.core.model.{CorData, Payload, Person}
import corsign.core.rsa.RSAKey
import corsign.core.validation.SimpleRSAValidator

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.DurationInt

object Standalone extends App {

  val uuid = UUID.randomUUID()
  println(s"Generating a new RSA key. $uuid")
  val key = RSAKey.generateNewRSAKey(Some(uuid))

  println("Private Key:")
  println(key.privateKeyPEM)
  println("Public Key:")
  println(key.publicKeyPEM)

  val now          = Instant.now
  val validity     = Instant.now.plusMillis(2.hours.toMillis)

  val claims = JWTClaims(
    UUID.randomUUID(),
    "issuer",
    "audience",
    validity.getEpochSecond,
    now.getEpochSecond,
    now.getEpochSecond,
    Payload(Person(UUID.randomUUID(), "firstname", "lastname"),
    CorData(Some(true))),
  )

  val token = JWTSigner.signWithRSA(claims, key)
  println("Signed Token with this Key is:")
  println(token)
  println("Parsed Content from validated Token is")
  println(SimpleRSAValidator.validateWithRSA(token, key).get)

  println(key.jwkJsonString)

  println("Now generating a QR Code")



}
