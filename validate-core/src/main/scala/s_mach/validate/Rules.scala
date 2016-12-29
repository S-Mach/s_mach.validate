package s_mach.validate

import s_mach.string.{CharGroup, CharGroupPattern}

object Rules {

  case class StringLengthMin(
    minInclusive: Int
  ) extends Rule {
    def key = StringLengthMin.key
    def args = minInclusive.toString :: Nil
  }
  object StringLengthMin {
    val key = 'string_length_min
  }

  val StringNonEmpty = StringLengthMin(1)

  case class StringLengthMax(
    maxInclusive: Int
  ) extends Rule {
    def key = StringLengthMax.key
    def args = maxInclusive.toString :: Nil

    def mkValidator : Validator[String] =
      Validator.ensure(this)(_.length <= maxInclusive)
  }
  object StringLengthMax {
    val key = 'string_length_max
  }

  object StringLengthRange {
    def apply(
      minInclusive: Int,
      maxInclusive: Int
    ) : List[Rule] =
      StringLengthMin(minInclusive) ::
      StringLengthMax(maxInclusive) ::
      Nil
  }


//  case class StringLengthRange(
//    minInclusive: Int,
//    maxInclusive: Int
//  ) extends Rule {
//    def key = StringLengthRange.key
//    def args = minInclusive.toString :: maxInclusive.toString :: Nil
//  }
//  object StringLengthRange {
//    val key = 'string_length_range
//
//  }

  // Note: JSONSchema specifies Ecma262 for pattern (http://json-schema.org/latest/json-schema-validation.html#anchor33)
  // Note: XML Schema specifies Unicode Regular Expressions level 1 (http://www.unicode.org/reports/tr18/#Basic_Unicode_Support)
  // For now, punting on resolving differences (which means some invalid schema patterns may be generated)
  case class StringPattern(
    pattern: String // Note: can't use Regex here since it doesn't implement equals correctly
  ) extends Rule {
    def key = StringPattern.key
    def args = pattern :: Nil
  }
  object StringPattern {
    val key = 'string_pattern
  }


  object StringCharGroupPattern {
    val key = 'string_pattern
    def apply(groups: CharGroup*) =
      StringPattern(CharGroupPattern(groups:_*).pattern.pattern)
  }
//  case class StringCharGroupPattern(
//    groups: Seq[CharGroup]
//  ) extends Rule {
//    val regex =CharGroupPattern(groups:_*)
//    def key = StringCharGroupPattern.key
//    def args = regex.pattern.pattern :: Nil
//  }
//  object StringCharGroupPattern {
//    val key = 'string_char_group_pattern
//  }

  val AllLetters =  StringCharGroupPattern(CharGroup.Letter)
  val AllDigits  = StringCharGroupPattern(CharGroup.Digit)
  val AllLettersOrDigits  = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit)
  val AllLettersOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Space)
  val AllLettersDigitsOrSpaces = StringCharGroupPattern(CharGroup.Letter,CharGroup.Digit,CharGroup.Space)

  case class NumberMinInclusive[N:Numeric](minInclusive: N) extends Rule {
    def _minInclusive: BigDecimal = BigDecimal(minInclusive.toString)

    def key = NumberMinInclusive.key
    def args = minInclusive.toString :: Nil
  }
  object NumberMinInclusive {
    val key = 'number_min_inclusive
  }

  case class NumberMinExclusive[N:Numeric](minExclusive: N) extends Rule {
    def _minExclusive: BigDecimal = BigDecimal(minExclusive.toString)

    def key = NumberMinExclusive.key
    def args = minExclusive.toString :: Nil
  }
  object NumberMinExclusive {
    val key = 'number_min_exclusive
  }

  case class NumberMaxInclusive[N:Numeric](maxInclusive: N) extends Rule {
    def _maxInclusive: BigDecimal = BigDecimal(maxInclusive.toString)

    def key = NumberMaxInclusive.key
    def args = maxInclusive.toString :: Nil
  }
  object NumberMaxInclusive {
    val key = 'number_max_inclusive
  }

  case class NumberMaxExclusive[N:Numeric](maxExclusive: N) extends Rule {
    def _maxExclusive: BigDecimal = BigDecimal(maxExclusive.toString)

    def key = NumberMaxExclusive.key
    def args = maxExclusive.toString :: Nil
  }
  object NumberMaxExclusive {
    val key = 'number_max_exclusive
  }

  object NumberRangeInclusive {
    def apply[N:Numeric](
      minInclusive: N,
      maxInclusive: N
    ) : List[Rule] =
      NumberMinInclusive(minInclusive) ::
      NumberMaxInclusive(maxInclusive) ::
      Nil
  }

  object NumberRangeExclusive {
    def apply[N:Numeric](
      minExclusive: N,
      maxExclusive: N
    ) : List[Rule] =
      NumberMinExclusive(minExclusive) ::
      NumberMaxExclusive(maxExclusive) ::
      Nil
  }

  object NumberRange {
    def apply[N:Numeric](
      minInclusive: N,
      maxInclusive: N
    ) : List[Rule] =
      NumberRangeInclusive(minInclusive,maxInclusive)
  }

//  case class NumberRangeInclusive[N:Numeric](
//    minInclusive: N,
//    maxInclusive: N
//  ) extends Rule {
//    def _minInclusive: BigDecimal = BigDecimal(minInclusive.toString)
//    def _maxInclusive: BigDecimal = BigDecimal(maxInclusive.toString)
//
//    def key = NumberRangeInclusive.key
//    def args = minInclusive.toString :: maxInclusive.toString :: Nil
//  }
//  object NumberRangeInclusive {
//    val key = 'number_range_inclusive
//  }
//
//  case class NumberRangeExclusive[N:Numeric](
//    minExclusive: N,
//    maxExclusive: N
//  ) extends Rule {
//    def _minExclusive: BigDecimal = BigDecimal(minExclusive.toString)
//    def _maxExclusive: BigDecimal = BigDecimal(maxExclusive.toString)
//
//    def key = NumberRangeExclusive.key
//    def args = minExclusive.toString :: maxExclusive.toString :: Nil
//  }
//  object NumberRangeExclusive {
//    val key = 'number_range_exclusive
//  }

  // TODO: ?
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

  //case class ExRule[A](key: Symbol, args: Seq[String]) extends Rule {
  //  def mkValidator(check: A => Boolean) : Validator[A] =
  //    Validator.ensure(this)(check)
  //}
}
