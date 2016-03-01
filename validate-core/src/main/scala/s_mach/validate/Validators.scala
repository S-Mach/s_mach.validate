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
package s_mach.validate

object Validators {

  object stringLengthMin {
    val key = Symbol("string.length.min.inclusive")
    def rule(min: Int) = {
      require(min > 0,"min must be greater than 0")
      Rule(key,min.toString)
    }
    def apply(min: Int) : Validator[String] =
      Validator.ensure(rule(min))(_.length >= min)
  }
  object stringLengthMax {
    val key = Symbol("string.length.max.inclusive")
    def rule(max: Int) = {
      require(max > 0,"max must be greater than 0")
      Rule(key,max.toString)
    }
    def apply(max: Int) : Validator[String] =
      Validator.ensure(rule(max))(_.length <= max)
  }
  object stringLengthRange {
    def apply(min: Int, max: Int) : Validator[String] = {
      stringLengthMin(min) and stringLengthMax(max)
    }
  }
  val stringNonEmpty = stringLengthMin(1)

  // Note: JSONSchema specifies Ecma262 for pattern (http://json-schema.org/latest/json-schema-validation.html#anchor33)
  // Note: XML Schema specifies Unicode Regular Expressions level 1 (http://www.unicode.org/reports/tr18/#Basic_Unicode_Support)
  // For now, punting on resolving differences (which means some invalid schema patterns may be generated)
  object stringPattern {
    val key = Symbol("string.pattern")
    def rule(pattern: String) = Rule(key,pattern)
    def apply(pattern: String) : Validator[String] = {
      val regex = pattern.r
      Validator.ensure(rule(pattern))(s => regex.findFirstIn(s).nonEmpty)
    }
  }

  object stringCharGroupPattern {
    def apply(groups: CharGroup*) =
      stringPattern(CharGroupPattern(groups:_*))
  }

  val allLetters = stringCharGroupPattern(CharGroup.Letter)
  val allDigits = stringCharGroupPattern(CharGroup.Digit)
  val allLettersOrDigits = stringCharGroupPattern(CharGroup.Digit,CharGroup.Letter)
  val allLettersOrSpaces = stringCharGroupPattern(CharGroup.Space,CharGroup.Letter)
  val allLettersDigitsOrSpaces = stringCharGroupPattern(CharGroup.Space,CharGroup.Digit,CharGroup.Letter)

  object numberMinInclusive {
    val key = Symbol("number.min.inclusive")
    def rule[N:Numeric](min: N) = Rule(key,min.toString)
    def apply[N:Numeric](min: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure(rule(min))(_ >= min)
    }
  }
  val numberMin = numberMinInclusive

  object numberMinExclusive {
    val key = Symbol("number.min.exclusive")
    def rule[N:Numeric](min: N) = Rule(key,min.toString)
    def apply[N:Numeric](min: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure(rule(min))(_ > min)
    }
  }
  object numberMaxInclusive {
    val key = Symbol("number.max.inclusive")
    def rule[N:Numeric](max: N) = Rule(key,max.toString)
    def apply[N:Numeric](max: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure(rule(max))(_ <= max)
    }
  }
  val numberMax = numberMaxInclusive

  object numberMaxExclusive {
    val key = Symbol("number.max.exclusive")
    def rule[N:Numeric](max: N) = Rule(key,max.toString)
    def apply[N:Numeric](max: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure(rule(max))(_ < max)
    }
  }

  object numberRangeInclusive {
    def apply[N:Numeric](min: N,max: N) : Validator[N] = {
      numberMinInclusive(min) and numberMaxInclusive(max)
    }
  }

  object numberRangeExclusive {
    def apply[N:Numeric](min: N,max: N) : Validator[N] = {
      numberMinExclusive(min) and numberMaxExclusive(max)
    }
  }

  val numberRange = numberRangeInclusive

// TODO:
//  // JSONSchema specific formats
//
//  val dateTime_rfc3339_5_6_regex = "".r
//  val dateTime_rfc3339_5_6 = {
//    Validator.ensure[String](
//      Rule(Symbol("datetime.rfc3339_5_6"))
//    )(s => dateTime_rfc3339_5_6_regex.findFirstIn(s).nonEmpty)
//  }
//
//  val email_rfc5322_3_4_1_regex = "".r
//  val email_rfc5322_3_4_1 = {
//    Validator.ensure[String](
//      Rule(Symbol("email.rfc5322_3_4_1"))
//    )(s => dateTime_rfc3339_5_6_regex.findFirstIn(s).nonEmpty)
//  }
//
//  val hostname_Rfc1034_3_1_regex = "".r
//  val hostname_Rfc1034_3_1 = {
//    Validator.ensure[String](
//      Rule(Symbol("hostname.Rfc1034_3_1"))
//    )(s => dateTime_rfc3339_5_6_regex.findFirstIn(s).nonEmpty)
//  }
//
//  val ipv4_rfc2673_3_2_regex = "".r
//  val ipv4_rfc2673_3_2 = {
//    Validator.ensure[String](
//      Rule(Symbol("ipv4.rfc2673_3_2"))
//    )(s => dateTime_rfc3339_5_6_regex.findFirstIn(s).nonEmpty)
//  }
//
//  val ipv6_rfc2373_2_2_regex = "".r
//  val ipv6_rfc2373_2_2 = {
//    Validator.ensure[String](
//      Rule(Symbol("ipv6.rfc2373_2_2"))
//    )(s => ipv6_rfc2373_2_2_regex.findFirstIn(s).nonEmpty)
//  }
//
//  val uri_rfc3986_regex = "".r
//  val uri_rfc3986 = {
//    Validator.ensure[String](
//      Rule(Symbol("uri.rfc3986"))
//    )(s => uri_rfc3986_regex.findFirstIn(s).nonEmpty)
//  }
}
