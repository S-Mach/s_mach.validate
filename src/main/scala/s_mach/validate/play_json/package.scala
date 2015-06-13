package s_mach.validate

import play.api.libs.json._
import play_json.PlayJsonUtils._

package object play_json {

  implicit def valueType_Reads[V <: IsValueType[A],A](implicit
    vt: ValueType[V,A],
    aReads: Reads[A]
  ) = Reads[V](js => aReads.reads(js).map(vt.apply))

  implicit def valueType_Writes[V <: IsValueType[A],A](implicit
    vt: ValueType[V,A],
    aWrites: Writes[A]
  ) = Writes[V](v => aWrites.writes(vt.unapply(v)))

  implicit class Net_SMach_Validate_PimpMyListOfExplain(val self: List[Explain]) extends AnyVal {
    def printJson: JsValue = JsonPrinter.print(self)
    def prettyPrintJson: String = Json.prettyPrint(printJson)
  }

  implicit class Net_SMach_Validate_PimpMyListOfRule(val self: List[Rule]) extends AnyVal {
    def toOptJsError : Option[JsError] = listRuleToJsError(self)
  }

  implicit class Net_SMach_Validate_PimpMyReads[A](val self:Reads[A]) extends AnyVal {
    def withValidator(implicit v: Validator[A]) : Reads[A] =
      Reads(wrapReadsWithValidator(self.reads,v))
  }

  implicit class Net_SMach_Validate_PimpMyFormat[A](val self:Format[A]) extends AnyVal {
    def withValidator(implicit v: Validator[A]) : Format[A] =
      Format(
        Reads(wrapReadsWithValidator(self.reads,v)),
        self
      )
  }
}
