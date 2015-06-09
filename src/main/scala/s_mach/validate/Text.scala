package s_mach.validate

object Text {
  val nonEmpty =
    Validator.ensure[String](
      "must not be empty"
    )(_.nonEmpty)

  def maxLength(maxLength: Int) =
    Validator.ensure[String](
      s"must not be longer than $maxLength characters"
    )(_.length <= maxLength)

  val allLetters =
    Validator.ensure[String](
      "must contain only letters"
    )(_.forall(_.isLetter))

  val allLettersOrDigits =
    Validator.ensure[String](
      "must contain only letters or digits"
    )(_.forall(_.isLetterOrDigit))

  val allLettersOrWhitespace =
    Validator.ensure[String](
      "must contain only letters or whitespace"
    )(_.forall(c => c.isLetter || c.isWhitespace))

  val allLettersDigitsOrWhitespace =
    Validator.ensure[String](
      "must contain only letters, digits or whitespace"
    )(_.forall(c => c.isLetterOrDigit || c.isWhitespace))
}