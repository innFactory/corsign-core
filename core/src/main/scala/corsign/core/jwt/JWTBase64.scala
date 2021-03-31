package corsign.core.jwt

object JWTBase64 {
  private lazy val encoder = java.util.Base64.getUrlEncoder()
  private lazy val decoder = java.util.Base64.getUrlDecoder()
  private lazy val decoderNonSafe = java.util.Base64.getDecoder()

  def encode(value: Array[Byte]): Array[Byte] = encoder.encode(value)
  def decode(value: Array[Byte]): Array[Byte] = decoder.decode(value)

  def encode(value: String): Array[Byte] = encode(JWTUtils.bytify(value))
  def decode(value: String): Array[Byte] = decoder.decode(value)

  def encodeString(value: Array[Byte]): String = encoder.encodeToString(value)
  def decodeString(value: Array[Byte]): String = JWTUtils.stringify(decode(value))

  def encodeString(value: String): String = encodeString(JWTUtils.bytify(value))
  def decodeString(value: String): String = decodeString(JWTUtils.bytify(value))

  def decodeNonSafe(value: Array[Byte]): Array[Byte] = decoderNonSafe.decode(value)
  def decodeNonSafe(value: String): Array[Byte] = decoderNonSafe.decode(value)
}

