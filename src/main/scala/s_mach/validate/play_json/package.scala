package s_mach.validate

import play.api.data.validation.ValidationError
import play.api.libs.json._

package object play_json {
  def listRuleToJsError(rules: List[Rule]) : Option[JsError] = {
    def pathToJsPath(path: String) : PathNode = {
      path match {
        case s if (s.size == 1 && s.head.isDigit) =>
          IdxPathNode(s.toInt)
        case _ => KeyPathNode(path)
      }
    }

    if(rules.isEmpty) {
      None
    } else {
      val errors =
        rules.groupBy(_.path).map { case (path,rules) =>
          val jsPath = JsPath(path.map(pathToJsPath))
          val errors = rules.map(r => ValidationError(r.desc))
          (jsPath,errors)
        }.toSeq
      Some(JsError(errors))
    }
  }

  implicit class Net_SMach_Validate_PimpMyListOfExplain(val self: List[Explain]) extends AnyVal {
    def printJson: JsValue = JsonPrinter.print(self)
    def prettyPrintJson: String = Json.prettyPrint(printJson)
    def toJsError : Option[JsError] = listRuleToJsError(self.collect { case r@Rule(_,_) => r })
  }

  implicit class Net_SMach_Validate_PimpMyListOfRule(val self: List[Rule]) extends AnyVal {
    def toJsError : Option[JsError] = listRuleToJsError(self)
  }

  def wrapReadsWithValidator[A](
    f: JsValue => JsResult[A],
    v:Validator[A]
  ) : JsValue => JsResult[A] = { json =>
    f(json).flatMap { a =>
      v(a).toJsError match {
        case None => JsSuccess(a)
        case Some(error) => error
      }
    }
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
