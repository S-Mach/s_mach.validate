package s_mach.validate

/**
 * Various common text validators
 */
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

  val allDigits =
    Validator.ensure[String](
      "must contain only digits"
    )(_.forall(_.isDigit))

  val allLettersOrDigits =
    Validator.ensure[String](
      "must contain only letters or digits"
    )(_.forall(_.isLetterOrDigit))

  val allLettersOrSpaces =
    Validator.ensure[String](
      "must contain only letters or spaces"
    )(_.forall(c => c.isLetter || c.isSpaceChar))

  val allLettersDigitsOrSpaces =
    Validator.ensure[String](
      "must contain only letters, digits or spaces"
    )(_.forall(c => c.isLetterOrDigit || c.isSpaceChar))

  val base64UrlSafeRegex = "[A-Za-z0-9-_]*".r
  val isBase64UrlSafe =
    Validator.ensure[String](
      "must be a base 64 url safe value ([A-Za-z0-9-_]*)"
    )(s => base64UrlSafeRegex.findFirstIn(s).nonEmpty)
}