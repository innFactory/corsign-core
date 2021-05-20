package corsign.core.qr

import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.apache.commons.codec.binary.Base64

case class B64QRCode() extends GenericQRCode[String] {
  def generate(qrInfo: QRData): String = {
    val prefix      = "data:image/png;base64,"
    val encodedByte = Base64.encodeBase64(
      QRCode
        .from(qrInfo.url)
        .to(ImageType.PNG)
        .withColor(qrInfo.onColor, qrInfo.offColor)
        .withSize(qrInfo.width, qrInfo.height)
        .withCharset("UTF-8")
        .withHint(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q)
        .stream()
        .toByteArray
    )

    prefix + new String(encodedByte)
  }
}
