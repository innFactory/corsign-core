package corsign.core.validation

import corsign.core.jwk.JWKUrl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CorsignJWTValidatorSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks {

  "#validate" should {
    "should create a custom" in {
      val correctlyConfiguredValidator =
        CorsignJWTValidator(JWKUrl("http://localhost"))
    }
  }
}
