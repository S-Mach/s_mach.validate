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

import play.api.libs.json._
import s_mach.metadata._
import s_mach.validate._
import s_mach.validate.Validators._
import s_mach.validate.play_json._

object JsonSchemaOps {
  import PlayJsonOps.pruneEmptyField
  
  def jsonTypeToString : JsonSchemaInfo.JsonType => String = {
    import JsonSchemaInfo._

    {
      case _:JsonBoolean => "boolean"
      case _:JsonString => "string"
      case _:JsonObject => "object"
      case _:JsonNumber => "number"
      case _:JsonInteger => "integer"
      case _:JsonArray => "array"
    }
  }

  def jsonSchemaRuleToMessage(jsonSchemaRule: JsonSchemaRule)(implicit mr:MessageForRule) : String = {
    import JsonSchemaRule._
    jsonSchemaRule match {
      case Maximum(value, exclusive) =>
        val ruleKey =
          if(exclusive) {
            Validators.numberMaxExclusive.key
          } else {
            Validators.numberMaxInclusive.key
          }
        mr.messageFor(Rule(ruleKey,value.toString()))
      case Minimum(value, exclusive) =>
        val ruleKey =
          if(exclusive) {
            Validators.numberMinExclusive.key
          } else {
            Validators.numberMinInclusive.key
          }
        mr.messageFor(Rule(ruleKey,value.toString()))
      case StringMaxLength(value) =>
        mr.messageFor(Rule(stringLengthMax.key,value.toString))
      case StringMinLength(value) =>
        mr.messageFor(Rule(stringLengthMin.key,value.toString))
      case StringPattern(value) =>
        mr.messageFor(Rule(stringPattern.key,value))
    }
  }
  
  def toTypeRemarks(tm: JsonSchema)(implicit mr:MessageForRule) : TypeRemarks = {
    import JsonSchemaInfo._
    tm.map {
      case OptMarker => Nil
      case jst:JsonType =>
        {
          jst match {
            case _:JsonObject => Nil
            case _ => s"must be ${jsonTypeToString(jst)}" :: Nil
          }
        } ::: {
          if(jst.isOptional) {
            "optional" :: Nil
          } else {
            Nil
          }
        } ::: {
          jst match {
            case jsv:JsonVal =>
              jsv.rules.map(jsonSchemaRuleToMessage)
            case _ => Nil
          }
        } :::
        jst.additionalRules :::
        jst.comments
    }
  }

  def printJsonSchema[A](id: String, head: JsonSchema)(implicit mr:MessageForRule) : JsObject = {
    import JsonSchemaInfo._

    def isOptionalField(field: (Field,TypeMetadata[JsonSchemaInfo])) : Boolean = {
      field._2.value match {
        case OptMarker => true
        case _ => false
      }
    }
    def maybeRules(rules: List[JsonSchemaRule]) : List[(String,JsValue)] = {
      import JsonSchemaRule._

      rules.flatMap {
        case Maximum(value, exclusive) =>
          List(
            "maximum" -> JsNumber(value)
          ) ::: {
            if(exclusive) {
              List(
                "exclusiveMaximum" -> JsBoolean(true)
              )
            } else {
              Nil
            }
          }
        case Minimum(value, exclusive) =>
          List(
            "minimum" -> JsNumber(value)
          )  ::: {
            if(exclusive) {
              List(
                "exclusiveMinimum" -> JsBoolean(true)
              )
            } else {
              Nil
            }
          }
        case StringMaxLength(value) =>
          List(
            "maxLength" -> JsNumber(value)
          )
        case StringMinLength(value) =>
          List(
            "minLength" -> JsNumber(value)
          )
        case StringPattern(value) =>
          List(
            "pattern" -> JsString(value)
          )
//        case MaxItems(value) => ???
//        case MinItems(value) => ???
      }
    }

    def maybeAdditionalRules(additionalRules: List[String]) : List[(String,JsValue)] =
      pruneEmptyField(JsArray(additionalRules.map(JsString)))("additionalRules" -> _)

    def maybeComments(comments: List[String]) : List[(String,JsValue)] =
      pruneEmptyField(JsArray(comments.map(JsString)))("comments" -> _)

    def loop(id: String, tm: JsonSchema) : JsObject = {
      tm match {
        case TypeMetadata.Val(value:JsonVal) =>
          JsObject(List(
            "id" -> JsString(id),
            "type" -> JsString(jsonTypeToString(value))
            ) :::
            maybeRules(value.rules) :::
            maybeAdditionalRules(value.additionalRules) :::
            maybeComments {
              value.comments ::: {
                // For string rule patterns that can be explained in plain language
                // without mentioning the pattern, add the explanation to the comments
                value.rules.collect {
                  case JsonSchemaRule.StringPattern(pattern) =>
                    val result = mr.messageFor(Rule(Validators.stringPattern.key,pattern))
                    if(result.contains(pattern)) {
                      None
                    } else {
                      Some(result)
                    }
                }.flatten
              }
            }
          )

        // Note: Option[A] isn't directly represented in JSON
        // Emit nothing for Option and recurse on A
        // JsonType for A will be marked optional
        case TypeMetadata.Arr(_,Cardinality.ZeroOrOne,member) =>
          loop(id,member)
        case TypeMetadata.Arr(value:JsonArray,cardinality,members) =>
          JsObject(List(
            "type" -> JsString("array"),
            "minItems" -> JsNumber {
              import Cardinality._
              cardinality match {
                case ZeroOrMore => 0
                case ZeroOrOne => 0
                case OneOrMore => 1
                case MinMax(min,_) => min
              }
            }) ::: {
              import Cardinality._
              cardinality match {
                case ZeroOrMore => Nil
                case ZeroOrOne => List("maxItems" -> JsNumber(1))
                case OneOrMore => Nil
                case MinMax(_,max) => List("maxItems" -> JsNumber(max))
              }
            } ::: List(
              "uniqueItems" -> JsBoolean(false),
              "additionalItems" -> JsBoolean(false),
              "items" -> loop(s"$id/1",members)
            ) :::
            maybeAdditionalRules(value.additionalRules) :::
            maybeComments(value.comments)
          )
        case r@TypeMetadata.Rec(value:JsonObject,_) =>
          JsObject(List(
            "id" -> JsString(id),
            "type" -> JsString("object"),
            "properties" -> JsObject(
              r.fields.map { case (field,tm) =>
                import field._
                field.name -> loop(s"$id/$name",tm)
              }
            ),
            "additionalProperties" -> JsBoolean(true),
            "required" -> JsArray(
              r.fields.collect { case t@(f,_) if isOptionalField(t) == false =>
                JsString(f.name)
              }
            )
          ) :::
          maybeAdditionalRules(value.additionalRules) :::
          maybeComments(value.comments)
        )
        case unsupported =>
          throw new UnsupportedOperationException(s"Unsupported TypeMetadata: $unsupported")
      }
    }
    Json.obj(
      "$schema" -> "http://json-schema.org/draft-04/schema#"
    ) ++
    loop(id,head)
  }

}
