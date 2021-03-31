package corsign.core.jwt

case class CorData (
    isNegative: Option[Boolean] = None,
    isVaccinated: Option[Boolean] = None,
    vaccine: Option[String] = None
){

  def toJson = "{}"
}
