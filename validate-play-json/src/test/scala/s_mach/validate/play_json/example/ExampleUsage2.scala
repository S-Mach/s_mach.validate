package s_mach.validate.play_json.example

// Extra examples for test coverage
object ExampleUsage2 {

import play.api.libs.json._
import s_mach.codetools._
import s_mach.validate._
import s_mach.validate.play_json._
import s_mach.validate.Validators._
import s_mach.validate.MessageForRule.Implicits._

implicit class WeightLb(
  val underlying: Double
) extends AnyVal with IsValueClass[Double]
object WeightLb {
  import scala.language.implicitConversions
  implicit val validator_WeightLb =
    // Create a Validator[WeightLb] based on a Validator[String]
    Validator.forValueClass[WeightLb, Double] {
      // Build a Validator[String] by composing some pre-defined validators
      _ and numberRangeExclusive(0.0,1000.0)
    }
  implicit val format_WeightLb =
    Json
      // Auto-generate a value-class format from the already existing implicit
      // Format[String]
      .forValueClass.format[WeightLb,Double]
      // Append the serialization-neutral Validator[WeightLb] to the Play JSON Format[WeightLb]
      .withValidator

  implicit val explainFormat_WeightLb =
    ExplainFormat.forValueClass[WeightLb,Double].withValidator
}
  
}
