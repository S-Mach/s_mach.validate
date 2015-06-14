package s_mach.validate.play_json

import play.api.data.validation.ValidationError
import play.api.libs.json._
import s_mach.validate.{Rule, Validator}

object PlayJsonUtils {
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

  def wrapReadsWithValidator[A](
    f: JsValue => JsResult[A],
    v:Validator[A]
  ) : JsValue => JsResult[A] = { json =>
    f(json).flatMap { a =>
      v(a).toOptJsError match {
        case None => JsSuccess(a)
        case Some(error) => error
      }
    }
  }

}
