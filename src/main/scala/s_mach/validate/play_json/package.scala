package s_mach.validate

import play.api.libs.json._

package object play_json {
  implicit class Net_SMach_Validate_PimpMyListOfExplain(val self: List[Explain]) extends AnyVal {

    def printJson: JsValue = {
      if(self.isEmpty) {
        JsNull
      } else {
        val ordered = self.sortBy(i => -i.path.size)
        val root = new ExplainNode()
        ordered.foreach(root.append)
        root.toJson
      }
    }
  }
}
