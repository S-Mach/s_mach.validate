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

import s_mach.explain_play_json._
import s_mach.validate._
import s_mach.explain_json._
import s_mach.i18n._

object ValidatePlayJsonOps {
  import JsonExplanationNode._

  // Note: this really belongs in its own repo validate_json
  // but seems overkill for now
  val pfRuleToJsonRule : PartialFunction[Rule,JsonRule] = {
    import JsonRule._

    {
      case Rules.StringLengthMin(minInclusive) =>
        StringMinLength(minInclusive)
      case Rules.StringLengthMax(maxInclusive)=>
        StringMaxLength(maxInclusive)
      case Rules.StringPattern(pattern) =>
        StringPattern(pattern)
      case Rules.NumberMinInclusive(minInclusive) =>
        Minimum(BigDecimal(minInclusive.toString),false)
      case Rules.NumberMinExclusive(minExclusive) =>
        Minimum(BigDecimal(minExclusive.toString),true)
      case Rules.NumberMaxInclusive(maxInclusive) =>
        Maximum(BigDecimal(maxInclusive.toString),false)
      case r@Rules.NumberMaxExclusive(maxExclusive) =>
        Maximum(BigDecimal(maxExclusive.toString),true)
    }
  }

  val ruleToMaybeJsonRule = pfRuleToJsonRule.lift

  def wrapExplainPlayJsonWithValidator[A](
    efa: ExplainPlayJson[A],
    va: Validator[A]
  ) : ExplainPlayJson[A] = ExplainPlayJson {

    val _rules =
      va.thisRules
        .map(rule => (rule,ruleToMaybeJsonRule(rule)))

    val moreRules = _rules.collect { case (_,Some(rule)) => rule }
    val moreAdditionalRules = _rules.collect { case (rule,None) =>
      { implicit cfg:I18NConfig => rule.i18n }
    }

    val newNode = {
      efa.explain.value match {
        case js@JsonArray(_, additionalRules, _) =>
          if (moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON array: $moreRules")
          }
          js.copy(
            additionalRules = additionalRules ::: moreAdditionalRules
          )

        case js@JsonObject(_, additionalRules, _) =>
          if (moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON object: $moreRules")
          }
          js.copy(
            additionalRules = additionalRules ::: moreAdditionalRules
          )

        case js@JsonBoolean(_, additionalRules, _) =>
          if (moreRules.nonEmpty) {
            throw new UnsupportedOperationException(s"Can't add Validator rules to JSON boolean: $moreRules")
          }
          js.copy(
            additionalRules = additionalRules ::: moreAdditionalRules
          )

        case js@JsonString(_, rules, additionalRules, _) =>
          js.copy(
            rules = rules ::: moreRules,
            additionalRules = additionalRules ::: moreAdditionalRules
          )

        case js@JsonNumber(_, rules, additionalRules, _) =>
          js.copy(
            rules = rules ::: moreRules,
            additionalRules = additionalRules ::: moreAdditionalRules
          )

        case js@JsonInteger(_, rules, additionalRules, _) =>
          js.copy(
            rules = rules ::: moreRules,
            additionalRules = additionalRules ::: moreAdditionalRules
          )
        case OptionMarker =>
          throw new UnsupportedOperationException("Can't add Validator rules to Option[A] marker")
      }
    }

    efa.explain.value(newNode)
  }
}
