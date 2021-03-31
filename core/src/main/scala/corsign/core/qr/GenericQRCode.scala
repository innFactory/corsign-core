package corsign.core.qr



trait GenericQRCode[T] {
  def generate(qrInfo: QRData): T
}


