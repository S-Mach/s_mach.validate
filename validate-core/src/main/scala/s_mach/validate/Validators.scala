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
        Rule.StringLengthMin(minInclusive)
      )(_.length >= minInclusive)
  }

  object StringLengthMax {
    def apply(maxInclusive: Int) : Validator[String] =
      Validator.ensure(
        Rule.StringLengthMax(maxInclusive)
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
        Rule.StringPattern(pattern)
      )(s => regex.findAllMatchIn(s).size == 1)
    }
  }

  object StringCharGroupPattern {
    def apply(groups: CharGroup*) : Validator[String] =
      StringPattern(Rule.StringCharGroupPattern(groups:_*).pattern)
  }

  val AllLetters =  StringCharGroupPattern(CharGroup.Letter)
  val AllDigits  = StringCharGroupPattern(CharGroup.Digit)
  val AllLettersOrDigits  = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit)
  val AllLettersOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Space)
  val AllLettersDigitsOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit,CharGroup.Space)

  // todo: cleanup move some where common?
  trait ToBigDecimal[N] {
    def toBigDecimal(n: N) : BigDecimal
  }
  
  implicit object toBigDecimal_Byte extends ToBigDecimal[Byte] {
    def toBigDecimal(n: Byte) = BigDecimal(n.toInt)
  }
  implicit object toBigDecimal_Short extends ToBigDecimal[Short] {
    def toBigDecimal(n: Short) = BigDecimal(n.toInt)
  }
  implicit object toBigDecimal_Int extends ToBigDecimal[Int] {
    def toBigDecimal(n: Int) = BigDecimal(n)
  }
  implicit object toBigDecimal_Long extends ToBigDecimal[Long] {
    def toBigDecimal(n: Long) = BigDecimal(n)
  }
  implicit object toBigDecimal_Float extends ToBigDecimal[Float] {
    def toBigDecimal(n: Float) = BigDecimal(n.toDouble)
  }
  implicit object toBigDecimal_Double extends ToBigDecimal[Double] {
    def toBigDecimal(n: Double) = BigDecimal(n)
  }

  implicit class S_Mach_Validate_EverythingPML[A](val self:A) extends AnyVal {
    def toBigDecimal(implicit toBigDecimal: ToBigDecimal[A]) : BigDecimal =
      toBigDecimal.toBigDecimal(self)
  }
  object NumberMinInclusive {
    def apply[N:Numeric:ToBigDecimal](minInclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rule.NumberMinInclusive(minInclusive.toBigDecimal)
      )(_ >= minInclusive)
    }
  }

  object NumberMinExclusive {
    def apply[N:Numeric:ToBigDecimal](minExclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rule.NumberMinExclusive(minExclusive.toBigDecimal)
      )(_ > minExclusive)
    }
  }

  object NumberMaxInclusive {
    def apply[N:Numeric:ToBigDecimal](maxInclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rule.NumberMaxInclusive(maxInclusive.toBigDecimal)
      )(_ <= maxInclusive)
    }
  }

  object NumberMaxExclusive {
    def apply[N:Numeric:ToBigDecimal](maxExclusive: N) : Validator[N] = {
      import Ordering.Implicits._
      Validator.ensure[N](
        Rule.NumberMaxExclusive(maxExclusive.toBigDecimal)
      )(_ < maxExclusive)
    }
  }

  object NumberRangeInclusive {
    def apply[N:Numeric:ToBigDecimal](minInclusive: N,maxInclusive: N) : Validator[N] = {
      NumberMinInclusive(minInclusive) and
      NumberMaxInclusive(maxInclusive)
    }
  }

  object NumberRangeExclusive {
    def apply[N:Numeric:ToBigDecimal](minExclusive: N,maxExclusive: N) : Validator[N] = {
      NumberMinExclusive(minExclusive) and
      NumberMaxExclusive(maxExclusive)
    }
  }

  object NumberRange {
    def apply[N:Numeric:ToBigDecimal](
      minInclusive: N,
      maxInclusive: N
    ) : Validator[N] =
      NumberRangeInclusive(minInclusive,maxInclusive)
  }
}
