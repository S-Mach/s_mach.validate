package s_mach.validate

import play.api.libs.json._
import play_json.PlayJsonUtils._

package object play_json {
  /* Prefix added to implicits to prevent shadowing: VJNoCcdSFL */

  object ValueClassJson {
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

  implicit class VJNoCcdSFL_JsonTypePML(val self: Json.type) extends AnyVal {
    def forValueClass = ValueClassJson
  }

  implicit class VJNoCcdSFL_ListExplainPML(val self: List[Explain]) extends AnyVal {
    def printJson: JsValue = JsonPrinter.print(self)
    def prettyPrintJson: String = Json.prettyPrint(printJson)
  }

  implicit class VJNoCcdSFL_ListRulePML(val self: List[Rule]) extends AnyVal {
    def toOptJsError : Option[JsError] = listRuleToJsError(self)
  }

  implicit class VJNoCcdSFL_ReadsPML[A](val self:Reads[A]) extends AnyVal {
    def withValidator(implicit v: Validator[A]) : Reads[A] =
      Reads(wrapReadsWithValidator(self.reads,v))
  }

  implicit class VJNoCcdSFL_FormatPML[A](val self:Format[A]) extends AnyVal {
    def withValidator(implicit v: Validator[A]) : Format[A] =
      Format(
        Reads(wrapReadsWithValidator(self.reads,v)),
        self
      )
  }
}
