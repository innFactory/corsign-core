package corsign.core.jwt

import java.security.{KeyFactory, PrivateKey, PublicKey, Signature}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import javax.crypto.{Mac, SecretKey}
import javax.crypto.spec.SecretKeySpec


object JWTUtils {
  val ENCODING = "UTF-8"
  val RSA = "RSA"
  val ECDSA = "EC"

  /** Convert an array of bytes to its corresponding string using the default encoding.
   *
   * @return the final string
   * @param arr the array of bytes to transform
   */
  def stringify(arr: Array[Byte]): String = new String(arr, ENCODING)

  /** Convert a string to its corresponding array of bytes using the default encoding.
   *
   * @return the final array of bytes
   * @param str the string to convert
   */
  def bytify(str: String): Array[Byte] = str.getBytes(ENCODING)

  private def escape(value: String): String = value.replaceAll("\"", "\\\\\"")

  /** Convert a sequence to a JSON array
   */
  def seqToJson(seq: Seq[Any]): String = if (seq.isEmpty) {
    "[]"
  } else {
    seq.map {
      case value: String => "\"" + escape(value) + "\""
      case value: Boolean => if (value) { "true" } else { "false" }
      case value: Double => value.toString
      case value: Short => value.toString
      case value: Float => value.toString
      case value: Long => value.toString
      case value: Int => value.toString
      case value: BigDecimal => value.toString
      case value: BigInt => value.toString
      case value: (String, Any) => hashToJson(Seq(value))
      case value: Any => "\"" + escape(value.toString) + "\""
    }.mkString("[", ",", "]")
  }

  /**
   * Convert a sequence of tuples to a JSON object
   */
  def hashToJson(hash: Seq[(String, Any)]): String = if (hash.isEmpty) {
    "{}"
  } else {
    hash.map {
      case (key, value: String) => "\"" + escape(key) + "\":\"" + escape(value) + "\""
      case (key, value: Boolean) => "\"" + escape(key) + "\":" + (if (value) { "true" } else { "false" })
      case (key, value: Double) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: Short) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: Float) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: Long) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: Int) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: BigDecimal) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: BigInt) => "\"" + escape(key) + "\":" + value.toString
      case (key, value: (String, Any)) => "\"" + escape(key) + "\":" + hashToJson(Seq(value))
      case (key, value: Seq[Any]) => "\"" + escape(key) + "\":" + seqToJson(value)
      case (key, value: Set[Any]) => "\"" + escape(key) + "\":" + seqToJson(value.toSeq)
      case (key, value: Any) => "\"" + escape(key) + "\":\"" + escape(value.toString) + "\""
    }.mkString("{", ",", "}")
  }

  /**
   * Merge multiple JSON strings to a unique one
   */
  def mergeJson(json: String, jsonSeq: String*): String = {
    val initJson = json.trim match {
      case "" => ""
      case value => value.drop(1).dropRight(1)
    }

    "{" + jsonSeq.map(_.trim).fold(initJson) {
      case (j1, result) if j1.length < 5 => result.drop(1).dropRight(1)
      case (result, j2) if j2.length < 7 => result
      case (j1, j2) => j1 + "," + j2.drop(1).dropRight(1)
    } + "}"
  }

  /*
  private def parseKey(key: String): Array[Byte] = JWTBase64.decodeNonSafe(
    key.replaceAll("-----BEGIN (.*)-----", "")
      .replaceAll("-----END (.*)-----", "")
      .replaceAll("\r\n", "")
      .replaceAll("\n", "")
      .trim
  )


  private def parsePrivateKey(key: String, keyAlgo: String) = {
    val spec = new PKCS8EncodedKeySpec(parseKey(key))
    KeyFactory.getInstance(keyAlgo).generatePrivate(spec)
  }

  private def parsePublicKey(key: String, keyAlgo: String): PublicKey = {
    val spec = new X509EncodedKeySpec(parseKey(key))
    KeyFactory.getInstance(keyAlgo).generatePublic(spec)
  }*/




}
