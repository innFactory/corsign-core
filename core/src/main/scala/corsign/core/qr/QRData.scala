package corsign.core.qr

import java.util.Objects

class QRData(val url: String, w: Int = 1024, h: Int = 1024, on: Int = 0xFF000000, off : Int = 0xFFFFFFFF) {
  val width: Int  = if (w < 256) 256 else w
  val height: Int = if (h < 256) 256 else h
  val onColor: Int = on
  val offColor: Int = off

  override def equals(obj: Any): Boolean =
    obj match {
      case that: QRData =>
        (this.url == that.url &&
          this.width == that.width &&
          this.height == that.height &&
          this.offColor == that.offColor &&
          this.onColor == that.onColor
          )
      case _            => false
    }
  override val hashCode                  = Objects.hash(url, Integer.valueOf(width), Integer.valueOf(height))
}

object QRData {
  def apply(url: String)                          = new QRData(url match {
    case null => ""
    case _    => url.trim()
  })
  def apply(url: String, width: Int, height: Int, onColor: Int = 0, offColor : Int = 255) = new QRData(
    url match {
      case null => ""
      case _    => url.trim()
    },
    width,
    height,
    onColor,
    offColor
  )

  def unapply(qrInfo: QRData): Option[(String, Int, Int)] =
    Option((qrInfo.url, qrInfo.width, qrInfo.height))
}
