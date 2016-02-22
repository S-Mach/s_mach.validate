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
       ;it1i ,,,:::;;;::1tti      s_mach.validate
         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.impl.play_json

import play.api.data.validation.ValidationError
import play.api.libs.json._
import s_mach.metadata._
import s_mach.validate._
import s_mach.validate.play_json._

object PlayJsonOps {
  def symbol(c: Cardinality) : String = {
    import Cardinality._
    c match {
      case ZeroOrOne => "?"
      case ZeroOrMore => "*"
      case OneOrMore => "+"
      case  MinMax(min: Int, max: Int) => s"{$min,$max}"
    }
  }

  def failuresToJsError(result: Metadata[List[Rule]])(implicit mr:MessageForRule) : Option[JsError] = {
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
            (jsPath,messages.value.map(rule => ValidationError(rule.message)))
          }
        ))
    }
  }

  def wrapReadsWithValidator[A](
    f: JsValue => JsResult[A],
    v:Validator[A]
  )(implicit mr:MessageForRule) : JsValue => JsResult[A] = { json =>
    f(json).flatMap { a =>
      failuresToJsError(v(a)) match {
        case None => JsSuccess(a)
        case Some(error) => error
      }
    }
  }

  // Note: not used currently, but saving this
//  def merge(js1: JsValue, js2: JsValue) : JsValue = {
//    (js1,js2) match {
//      case (JsNull,_) => js2
//      case (_,JsNull) => js1
//      case (JsUndefined(),_) => js2
//      case (_,JsUndefined()) => js1
//      case (JsArray(members1),JsArray(members2)) =>
//        JsArray(members1 ++ members2)
//      case (o1@JsObject(fields1),o2@JsObject(fields2)) =>
//        val fields =
//          (fields1.zip(Stream.from(0,2)) ++ fields2.zip(Stream.from(1,2)))
//            .map {
//              // always bump 'this' field to top of list
//              case (("this",jsv),_) => (("this",jsv),-1)
//              case t => t
//            }
//            .groupBy(_._1._1)
//            .mapValues(
//              _
//                .reduce { (t1,t2) =>
//                  val ((fieldName,jsv1),idx1) = t1
//                  val ((_,jsv2),idx2) = t2
//                  ((fieldName,merge(jsv1,jsv2)),Math.min(idx1,idx2))
//                }
//            )
//            .toSeq
//            .sortBy(_._2._2)
//            .map { case (fieldName,((_,jsv),_)) => (fieldName,jsv) }
//
//        JsObject(fields)
//      case (JsArray(members),jsv) =>
//        JsArray(members :+ jsv)
//      case (jsv,JsArray(members)) =>
//        JsArray(jsv +: members)
//      case _ =>
//        JsArray(Seq(js1,js2))
//    }
//  }

  def isNonEmptyJs(js: JsValue) : Boolean =
    js match {
      case JsNull | JsArray(Seq()) | JsObject(Seq()) => false
      case _ => true
    }

  def pruneEmptyField(js: JsValue)(f: JsValue => (String,JsValue)) : List[(String,JsValue)] = {
    if(isNonEmptyJs(js)) {
      List(f(js))
    } else {
      Nil
    }
  }

  def printMetadataJs[A](
    m: Metadata[A]
  )(
    f: A => JsValue
  )(implicit
    cfg: PrintJsConfig
  ) : JsValue = {
    import cfg._

    def loop : Metadata[A] => JsValue = {
      case Metadata.Val(value) => f(value)
      case Metadata.Arr(value,Cardinality.ZeroOrOne,members) =>
        if(collapseOption) {
          members.map(t => loop(t._2)).headOption.getOrElse(JsNull)
        } else {
          JsObject(
            pruneEmptyField(f(value))("this" -> _) ++ {
            members.flatMap { member =>
              pruneEmptyField(loop(member._2))(member._1.toString -> _)
            }}
          )
        }
      case Metadata.Arr(value,cardinality,members) =>
        JsObject(
          pruneEmptyField(f(value))("this" -> _) ++ {
          members.flatMap { member =>
            pruneEmptyField(loop(member._2))(member._1.toString -> _)
          }}
        )
      case Metadata.Rec(value,fields) =>
        JsObject(
          pruneEmptyField(f(value))("this" -> _) ++
          fields.flatMap { case (field,metadata) =>
            import field._
            pruneEmptyField(loop(metadata))(name -> _)
          }
        )
    }
    loop(m)
  }

  def printTypeMetadataJs[A](
    tm: TypeMetadata[A]
  )(
    f: A => JsValue
  )(implicit
    cfg: PrintJsConfig
  ) : JsValue = {
    import cfg._
    def loop : TypeMetadata[A] => JsValue = {
      case TypeMetadata.Val(value) => f(value)
      case TypeMetadata.Arr(value,Cardinality.ZeroOrOne,members) =>
        if(collapseOption) {
          loop(members)
        } else {
          JsObject(
            pruneEmptyField(f(value))("this" -> _) ++
            pruneEmptyField(loop(members))(symbol(Cardinality.ZeroOrOne) -> _)
          )
        }
      case TypeMetadata.Arr(value,cardinality,members) =>
        JsObject(
          pruneEmptyField(f(value))("this" -> _) ++
          pruneEmptyField(loop(members))(symbol(cardinality) -> _)
        )
      case r@TypeMetadata.Rec(value,_) =>
        JsObject(
          pruneEmptyField(f(value))("this" -> _) ++
          r.fields.flatMap { case (field,typeMetadata) =>
            import field._
            pruneEmptyField(loop(typeMetadata))(name -> _)
          }
        )
    }
    loop(tm)
  }

  def printJsTypeRemarks(tr: TypeRemarks) : JsValue = {
    printTypeMetadataJs(tr) { remarks =>
      if(remarks.nonEmpty) {
        JsArray(remarks.map(JsString))
      } else {
        JsNull
      }
    }
  }

  def printJsRemarks(r: Remarks) : JsValue = {
    printMetadataJs(r) { remarks =>
      if(remarks.nonEmpty) {
        JsArray(remarks.map(JsString))
      } else {
        JsNull
      }
    }
  }


}
