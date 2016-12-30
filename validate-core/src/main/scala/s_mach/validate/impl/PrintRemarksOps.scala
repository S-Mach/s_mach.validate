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
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.impl

import s_mach.string._
import s_mach.i18n._
import s_mach.validate._
import ValidatorMessages._
import Rules._
// todo: this should be somewhere common
import s_mach.explain_json.impl.JsonExplanationOps.explainCharGroups

object PrintRemarksOps {

  def rulePrintRemarks(rule: Rule)(implicit i18ncfg: I18NConfig) : I18NString = rule match {
      /*
        Note: java.text.NumberFormat is used by default for I18N for number types
        which doesn't always preserve significant digits for BigDecimal
        Example:

        scala> java.text.NumberFormat.getNumberInstance(java.util.Locale.getDefault).format(BigDecimal("0.0"))
        res0: String = 0
       */
    case StringLengthMin(minInclusive) =>
      minInclusive match {
        case 1 =>
          m_must_not_be_empty()
        case n =>
          m_must_have_at_least_$int_characters(n)
      }
    case StringLengthMax(maxInclusive) =>
      m_must_not_be_longer_than_$int_characters(maxInclusive)
    case StringPattern(pattern) =>
      CharGroupPattern.unapplySeq(pattern).map(explainCharGroups).getOrElse {
        m_must_match_regex_pattern_$pattern(pattern.asI18N)
      }
    case rule@NumberMinInclusive(_) =>
      m_must_be_greater_than_or_equal_to_$number(rule.minInclusive)
    case rule@NumberMinExclusive(_) =>
      m_must_be_greater_than_$number(rule.minExclusive)
    case rule@NumberMaxInclusive(_) =>
      m_must_be_less_than_or_equal_to_$number(rule.maxInclusive)
    case rule@NumberMaxExclusive(_) =>
      m_must_be_less_than_$number(rule.maxExclusive)
    case _ =>
      if(rule.args.nonEmpty) {
        i18ncfg.resolver.resolveInterpolation(rule.key,rule.args:_*)
      } else {
        i18ncfg.resolver.resolveLiteral(rule.key)
      }

  }
}
