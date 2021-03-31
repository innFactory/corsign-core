package corsign.core.qr

import java.util.Objects
import org.apache.commons.codec.binary.Base64

class QRData(val url: String, w: Int = 1024, h: Int = 1024) {
  val width: Int  = if (w < 256) 256 else w
  val height: Int = if (h < 256) 256 else h

  override def equals(obj: Any): Boolean =
    obj match {
      case that: QRData =>
        (this.url == that.url &&
          this.width == that.width &&
          this.height == that.height)
      case _            => false
    }
  override val hashCode                  = Objects.hash(url, Integer.valueOf(width), Integer.valueOf(height))
}

object QRData {
  def apply(url: String)                          = new QRData(url match {
    case null => ""
    case _    => url.trim()
  })
  def apply(url: String, width: Int, height: Int) = new QRData(
    url match {
      case null => ""
      case _    => url.trim()
    },
    width,
    height
  )

  def unapply(qrInfo: QRData): Option[(String, Int, Int)] =
    Option((qrInfo.url, qrInfo.width, qrInfo.height))
}
