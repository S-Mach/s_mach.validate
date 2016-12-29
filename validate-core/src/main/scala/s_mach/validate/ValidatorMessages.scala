package s_mach.validate

import s_mach.i18n.I18NString
import s_mach.i18n.messages._

object ValidatorMessages {
  val m_must_not_be_empty = 'm_must_not_be_empty.literal
  val m_must_have_at_least_$int_characters = 'm_must_have_at_least_$int_characters.m[Int]
  val m_must_not_be_longer_than_$int_characters = 'm_must_not_be_longer_than_$int_characters.m[Int]
  val m_must_be_between_$int_and_$int_characters = 'm_must_be_between_$int_and_$int_characters.m[Int,Int]
  val m_must_match_regex_pattern_$pattern = 'm_must_match_regex_pattern_$pattern.m[I18NString]
  val m_must_be_greater_than_or_equal_to_$number = 'm_must_be_greater_than_or_equal_to_$number.m[BigDecimal]
  val m_must_be_greater_than_$number = 'm_must_be_greater_than_$number.m[BigDecimal]
  val m_must_be_less_than_or_equal_to_$number = 'm_must_be_less_than_or_equal_to_$number.m[BigDecimal]
  val m_must_be_less_than_$number = 'm_must_be_less_than_$number.m[BigDecimal]
}
