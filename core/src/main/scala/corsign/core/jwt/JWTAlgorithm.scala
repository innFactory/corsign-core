package corsign.core.jwt

import com.nimbusds.jose.JWSAlgorithm

sealed trait JWTAlgorithm {
  def name: String
  def fullName: String
  def nimbusRepresentation: JWSAlgorithm
}

sealed trait JwtAsymmetricAlgorithm extends JWTAlgorithm {}

sealed trait JwtHmacAlgorithm extends JWTAlgorithm {}
sealed trait JwtRSAAlgorithm extends JwtAsymmetricAlgorithm {}


object JWTAlgorithm {


  def fromString(algo: String): JWTAlgorithm = algo match {
    case "HS256"       => HS256
    case "HS384"       => HS384
    case "HS512"       => HS512
    case "RS256"       => RS256
    case "RS384"       => RS384
    case "RS512"       => RS512
    case _         => RS512
  }

  def optionFromString(algo: String): Option[JWTAlgorithm] = if (algo == "none") {
    None
  } else {
    Some(fromString(algo))
  }

  def allHmac(): Seq[JwtHmacAlgorithm] = Seq(HS256, HS384, HS512)

  def allAsymmetric(): Seq[JwtAsymmetricAlgorithm] = Seq(RS256, RS384, RS512)

  def allRSA(): Seq[JwtRSAAlgorithm] = Seq(RS256, RS384, RS512)


  case object HS256 extends JwtHmacAlgorithm  { def name = "HS256"; def fullName = "HmacSHA256"; def nimbusRepresentation = JWSAlgorithm.HS256 }
  case object HS384 extends JwtHmacAlgorithm  { def name = "HS384"; def fullName = "HmacSHA384"; def nimbusRepresentation = JWSAlgorithm.HS384 }
  case object HS512 extends JwtHmacAlgorithm  { def name = "HS512"; def fullName = "HmacSHA512"; def nimbusRepresentation = JWSAlgorithm.HS512 }
  case object RS256 extends JwtRSAAlgorithm   { def name = "RS256"; def fullName = "SHA256withRSA"; def nimbusRepresentation = JWSAlgorithm.RS256  }
  case object RS384 extends JwtRSAAlgorithm   { def name = "RS384"; def fullName = "SHA384withRSA"; def nimbusRepresentation = JWSAlgorithm.RS384  }
  case object RS512 extends JwtRSAAlgorithm   { def name = "RS512"; def fullName = "SHA512withRSA"; def nimbusRepresentation = JWSAlgorithm.RS512  }
}

