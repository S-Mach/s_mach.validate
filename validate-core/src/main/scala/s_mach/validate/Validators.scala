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

import s_mach.string._

object Validators {
  object StringLengthMin {
    def apply(minInclusive: Int) : Validator[String] =
      Validator.ensure(
        Rules.StringLengthMin(minInclusive)
      )(_.length >= minInclusive)
  }

  object StringLengthMax {
    def apply(maxInclusive: Int) : Validator[String] =
      Validator.ensure(
        Rules.StringLengthMax(maxInclusive)
      )(_.length <= maxInclusive)
  }

  val StringNonEmpty = StringLengthMin(1)

  object StringLengthRange {
    def apply(
      minInclusive: Int,
      maxInclusive: Int
    ) : Validator[String] =
      StringLengthMin(minInclusive) and
      StringLengthMax(maxInclusive)
  }

  object StringPattern {
    def apply(pattern: String) : Validator[String] = {
      val regex = pattern.r
      Validator.ensure(
        Rules.StringPattern(pattern)
      )(s => regex.findAllMatchIn(s).size == 1)
    }
  }

  object StringCharGroupPattern {
    def apply(groups: CharGroup*) : Validator[String] =
      StringPattern(Rules.StringCharGroupPattern(groups:_*).pattern)
  }

  val AllLetters =  StringCharGroupPattern(CharGroup.Letter)
  val AllDigits  = StringCharGroupPattern(CharGroup.Digit)
  val AllLettersOrDigits  = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit)
  val AllLettersOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Space)
  val AllLettersDigitsOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit,CharGroup.Space)

  object NumberMinInclusive {
    def apply[N:Numeric](minInclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rules.NumberMinInclusive(minInclusive)
      )(_ >= minInclusive)
    }
  }

  object NumberMinExclusive {
    def apply[N:Numeric](minExclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rules.NumberMinExclusive(minExclusive)
      )(_ > minExclusive)
    }
  }

  object NumberMaxInclusive {
    def apply[N:Numeric](maxInclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rules.NumberMaxInclusive(maxInclusive)
      )(_ <= maxInclusive)
    }
  }

  object NumberMaxExclusive {
    def apply[N:Numeric](maxExclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rules.NumberMaxExclusive(maxExclusive)
      )(_ < maxExclusive)
    }
  }

  object NumberRangeInclusive {
    def apply[N:Numeric](minInclusive: N,maxInclusive: N) : Validator[N] = {
      NumberMinInclusive(minInclusive) and
      NumberMaxInclusive(maxInclusive)
    }
  }

  object NumberRangeExclusive {
    def apply[N:Numeric](minExclusive: N,maxExclusive: N) : Validator[N] = {
      NumberMinExclusive(minExclusive) and
      NumberMaxExclusive(maxExclusive)
    }
  }

  object NumberRange {
    def apply[N:Numeric](
      minInclusive: N,
      maxInclusive: N
    ) : Validator[N] =
      NumberRangeInclusive(minInclusive,maxInclusive)
  }
}
