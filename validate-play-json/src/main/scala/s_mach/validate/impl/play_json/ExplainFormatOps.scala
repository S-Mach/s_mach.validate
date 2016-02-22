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

import scala.language.higherKinds
import s_mach.codetools.IsValueClass
import s_mach.metadata._
import s_mach.validate._
import s_mach.validate.play_json._

object ExplainFormatOps {
  case class ExplainFormatImpl[A](jsonSchema: JsonSchema) extends ExplainFormat[A]

  /** @return an ExplainFormat for a value */
  def forVal[A](jsi: JsonSchemaInfo.JsonVal) : ExplainFormat[A] = ExplainFormatImpl[A](
    TypeMetadata.Val(jsi)
  )

  /** @return an ExplainFormat for a value class */
  def forValueClass[V <: IsValueClass[A],A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[V] = ExplainFormatImpl[V](ea.jsonSchema)

  /** @return an ExplainFormat for a distinct type alias */
  def forDistinctTypeAlias[V <: A,A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[V] = ExplainFormatImpl[V](ea.jsonSchema)

  implicit def forOption[A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[Option[A]] = {
    ExplainFormatImpl {
      TypeMetadata.Arr(
        // Note: Option[A] in JSON is represented as "optional" A
        // renders should simply throw away this wrapper and mark the
        // the contained jsonSchema as optional
        // Wrapper is required for preserving normal TypeMetadata structure
        JsonSchemaInfo.OptMarker,
        Cardinality.ZeroOrOne,
        ea.jsonSchema.value {
          ea.jsonSchema.value match {
            case jst:JsonSchemaInfo.JsonType =>
              jst.isOptional(true)
            case JsonSchemaInfo.OptMarker =>
              throw new UnsupportedOperationException("Can't make OptMarker optional")
          }
        }
      )
    }

    //    ExplainFormatImpl[Option[A]] {
//      // Note: Instead of TypeMetadata.Arr like Traversable below, Option is
//      // collapsed here to a copy of the inner with isOptional set true since
//      // in Format.writes optional fields values are not represented as arrays
//      // but instead are omitted if not set and emitted if set
//      ea.jsonSchema.value(ea.jsonSchema.value.copy(isOptional = true))
//    }
  }

  implicit def forTraversable[M[AA] <: Traversable[AA],A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[M[A]] =
    ExplainFormatImpl[M[A]] {
      TypeMetadata.Arr(
        // Note: Option[A] is represented in JSON by "optional" A
        // the Option is not actually emitted
        JsonSchemaInfo.JsonArray(),
        Cardinality.ZeroOrMore,
        ea.jsonSchema
      )
    }

  val pfRuleToJsonSchemaRule : PartialFunction[Rule,JsonSchemaRule] = {
    import JsonSchemaRule._

    {
      case Rule(Validators.stringLengthMin.key,params) =>
        StringMinLength(params.head.toInt)
      case Rule(Validators.stringLengthMax.key,params)=>
        StringMaxLength(params.head.toInt)
      case Rule(Validators.stringPattern.key,params) =>
        StringPattern(params.head)
      case Rule(Validators.numberMinInclusive.key,params) =>
        Minimum(BigDecimal(params.head),false)
      case Rule(Validators.numberMinExclusive.key,params) =>
        Minimum(BigDecimal(params.head),true)
      case Rule(Validators.numberMaxInclusive.key,params) =>
        Maximum(BigDecimal(params.head),false)
      case Rule(Validators.numberMaxExclusive.key,params) =>
        Maximum(BigDecimal(params.head),true)
    }
  }

  def wrapExplainFormatWithValidator[A](
    efa: ExplainFormat[A],
    va: Validator[A]
  )(implicit
    mr: MessageForRule
  ) : ExplainFormat[A] = {
    import JsonSchemaInfo._

    val _rules =
      va.thisRules
        .map(rule => (rule,pfRuleToJsonSchemaRule.lift(rule)))

    val moreRules = _rules.collect { case (_,Some(jsonSchemaRule)) => jsonSchemaRule }
    val moreAdditionalRules = _rules.collect { case (rule,None) => rule.message }

    val newJsonSchemaInfo =
      efa.jsonSchema.value match {
        case OptMarker =>
          throw new UnsupportedOperationException("Can't add Validator rules to Option[A] marker")

        case JsonArray(isOptional,additionalRules,comments) =>
          if(moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON array: $moreRules")
          }
          JsonArray(isOptional,additionalRules ::: moreAdditionalRules,comments)
        case JsonObject(isOptional,additionalRules,comments) =>
          if(moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON object: $moreRules")
          }
          JsonObject(isOptional,additionalRules ::: moreAdditionalRules,comments)

        case JsonBoolean(isOptional,additionalRules,comments) =>
          if(moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON boolean: $moreRules")
          }
          JsonBoolean(isOptional,additionalRules ::: additionalRules, comments)


        case JsonString(isOptional,rules,additionalRules,comments) =>
          JsonString(isOptional,rules ::: moreRules, additionalRules ::: additionalRules, comments)
        case JsonNumber(isOptional,rules,additionalRules,comments) =>
          JsonNumber(isOptional,rules ::: moreRules, additionalRules ::: additionalRules, comments)
        case JsonInteger(isOptional,rules,additionalRules,comments) =>
          JsonInteger(isOptional,rules ::: moreRules, additionalRules ::: additionalRules, comments)
      }

    ExplainFormat(
      efa.jsonSchema.value(newJsonSchemaInfo)
    )
  }
}
