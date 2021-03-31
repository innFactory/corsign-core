package corsign.core.app

import corsign.core.jwt.{CorData, JWTClaims, JWTSigner, Subject}
import corsign.core.rsa.RSAKey
import corsign.core.validation.SimpleRSAValidator

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.DurationInt

object Standalone extends App {

  val key = RSAKey.generateNewRSAKey(UUID.randomUUID())
  val key2 = RSAKey.generateNewRSAKey(UUID.randomUUID())
  val ptn = List(
  key.publicKeyPEM,
  key.privateKeyPEM
  )
  val now          = Instant.now
  val validity     = Instant.now.plusMillis(2.hours.toMillis)

  val claims = JWTClaims(
    "issuer",
    "audience",
    validity.getEpochSecond,
    now.getEpochSecond,
    now.getEpochSecond,
      Subject(UUID.randomUUID(), "firstname", "lastname"),
    CorData(Some(true)),
  )

  val token = JWTSigner.signWithRSA(claims, key)
  println("-- --")
  println(SimpleRSAValidator.validateWithRSA(token, key))
  println("-- --")


  ptn.foreach(println)
}
