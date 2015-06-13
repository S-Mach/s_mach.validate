package s_mach.validate

object DataTypeValidatorImplicits extends DataTypeValidatorImplicits
trait DataTypeValidatorImplicits {
  implicit val validator_Byte = Validator.schema[Byte]()
  implicit val validator_Short = Validator.schema[Short]()
  implicit val validator_Int = Validator.schema[Int]()
  implicit val validator_Long = Validator.schema[Long]()
  implicit val validator_Float = Validator.schema[Float]()
  implicit val validator_Double = Validator.schema[Double]()
  implicit val validator_Char = Validator.schema[Char]()
  implicit val validator_String = Validator.schema[String]()
  implicit val validator_BigInt = Validator.schema[BigInt]()
  implicit val validator_BigDecimal = Validator.schema[BigDecimal]()
}
