/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      s_mach.validate-play_json
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.play_json.impl

import play.api.data.validation.ValidationError
import play.api.libs.json._
import s_mach.i18n._
import s_mach.metadata._
import s_mach.validate._

object PlayJsonOps {
  def failuresToJsError(result: Metadata[List[Rule]])(implicit i18ncfg:I18NConfig) : Option[JsError] = {
    result.nodes.filter(_._2.value.nonEmpty) match {
      case Nil => None
      case failures =>
        Some(JsError(
          failures.toStream.map { case (path,messages) =>
            val jsPath =
              JsPath(path.map{
                case Metadata.PathNode.SelectField(name) =>
                  KeyPathNode(name)
                case Metadata.PathNode.SelectMember(_,index) =>
                  IdxPathNode(index)
              })
            (jsPath,messages.value.map(rule => ValidationError(rule.i18n)))
          }
        ))
    }
  }

  def wrapReadsWithValidator[A](
    f: JsValue => JsResult[A],
    v:Validator[A]
  )(implicit i18ncfg: I18NConfig) : JsValue => JsResult[A] = { json =>
    f(json).flatMap { a =>
      failuresToJsError(v(a)) match {
        case None => JsSuccess(a)
        case Some(error) => error
      }
    }
  }
}