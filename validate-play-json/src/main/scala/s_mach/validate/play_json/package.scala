package s_mach.validate

import play.api.libs.json._
import play_json.PlayJsonUtils._

package object play_json {

  object ValueTypeJson {
    def writes[V <: IsValueClass[A],A](implicit
      aWrites:Writes[A]
    ) : Writes[V] =
      Writes[V](v => aWrites.writes(v.underlying))

    def reads[V <: IsValueClass[A],A](f: A => V)(implicit
      aReads:Reads[A]
    ) : Reads[V] =
      Reads[V](js => aReads.reads(js).map(f))

    def format[V <: IsValueClass[A],A](
      f: A => V
    )(implicit
      aReads: Reads[A],
      aWrites: Writes[A]
    ) : Format[V] = Format(reads(f),writes)
  }

  implicit class Net_SMach_Validate_PimpJsonType(val self: Json.type) extends AnyVal {
    def forValueClass = ValueTypeJson
  }

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
