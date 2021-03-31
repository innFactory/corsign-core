package corsign.core.jwt

import corsign.core.jwt.Gender.Gender

import java.util.{Date, UUID}


case class Subject(
                    uuid: UUID,
                    firstname: String,
                    lastname: String,
                    gender: Option[Gender] = None,
                    birthday: Option[Date] = None,
                    phoneNumber: Option[String] = None,
                    email : Option[String] = None,
                    idCardNumber: Option[String] = None,
                  ){

  def toJson = "{}"
}

