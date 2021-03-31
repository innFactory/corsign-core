package corsign.core.jwt


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




}
