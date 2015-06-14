package s_mach.validate

object DataTypeValidatorImplicits extends DataTypeValidatorImplicits
trait DataTypeValidatorImplicits {
  implicit val validator_Byte = Validator.empty[Byte]
  implicit val validator_Short = Validator.empty[Short]
  implicit val validator_Int = Validator.empty[Int]
  implicit val validator_Long = Validator.empty[Long]
  implicit val validator_Float = Validator.empty[Float]
  implicit val validator_Double = Validator.empty[Double]
  implicit val validator_Char = Validator.empty[Char]
  implicit val validator_String = Validator.empty[String]
  implicit val validator_BigInt = Validator.empty[BigInt]
  implicit val validator_BigDecimal = Validator.empty[BigDecimal]
}
