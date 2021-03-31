/*package corsign.core.jwt

object JWTHeader {
  val DEFAULT_TYPE = "JWT"

  def apply(
             algorithm: Option[JWTAlgorithm] = None,
             typ: Option[String] = None,
             contentType: Option[String] = None,
             keyId: Option[String] = None
           ) = new JWTHeader(algorithm, typ, contentType, keyId)

  def apply(algorithm: Option[JWTAlgorithm]): JWTHeader = algorithm match {
    case Some(algo) => JWTHeader(algo)
    case _ => new JWTHeader(None, None, None, None)
  }

  def apply(algorithm: JWTAlgorithm): JWTHeader = new JWTHeader(Option(algorithm), Option(DEFAULT_TYPE), None, None)

  def apply(algorithm: JWTAlgorithm, typ: String): JWTHeader = new JWTHeader(Option(algorithm), Option(typ), None, None)

  def apply(algorithm: JWTAlgorithm, typ: String, contentType: String): JWTHeader =
    new JWTHeader(Option(algorithm), Option(typ), Option(contentType), None)

  def apply(algorithm: JWTAlgorithm, typ: String, contentType: String, keyId: String): JWTHeader =
    new JWTHeader(Option(algorithm), Option(typ), Option(contentType), Option(keyId))
}

class JWTHeader(
                 val algorithm: Option[JWTAlgorithm],
                 val typ: Option[String],
                 val contentType: Option[String],
                 val keyId: Option[String]
               ) {
  def toJson: String = JWTUtils.hashToJson(Seq(
    "typ" -> typ,
    "alg" -> algorithm.map(_.name).orElse(Option("none")),
    "cty" -> contentType,
    "kid" -> keyId
  ).collect {
    case (key, Some(value)) => (key -> value)
  })

  /** Assign the type to the header */
  def withType(typ: String): JWTHeader = {
    JWTHeader(algorithm, Option(typ), contentType, keyId)
  }

  /** Assign the default type `JWT` to the header */
  def withType: JWTHeader = this.withType(JWTHeader.DEFAULT_TYPE)

  /** Assign a key id to the header */
  def withKeyId(keyId: String): JWTHeader = {
    JWTHeader(algorithm, typ, contentType, Option(keyId))
  }

  // equality code
  def canEqual(other: Any): Boolean = other.isInstanceOf[JWTHeader]

  override def equals(other: Any): Boolean = other match {
    case that: JWTHeader =>
      (that canEqual this) &&
        algorithm == that.algorithm &&
        typ == that.typ &&
        contentType == that.contentType &&
        keyId == that.keyId
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(algorithm, typ, contentType, keyId)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
*/