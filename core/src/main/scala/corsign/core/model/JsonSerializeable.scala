package corsign.core.model

import play.api.libs.json.JsValue

trait JsonSerializeable {
  def toJson: JsValue

}
