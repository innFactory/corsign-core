package corsign.core.jwt

case class JWTClaims(
  iss: String,
  aud: String,
  exp: Long,
  nbf: Long,
  iat: Long,
  sub: Subject,
  corData: CorData) {
}
