package s_mach.validate.play_json

import java.util.Locale

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json
import s_mach.codetools.IsValueClass
import s_mach.codetools.play_json._
import s_mach.explain_json.JsonExplanationNode._
import s_mach.explain_play_json._
import s_mach.validate._
import s_mach.i18n._
import s_mach.i18n.messages._
import s_mach.validate.play_json.impl.ValidatePlayJsonOps

object ExplainPlayJsonWithValidatorTest {
  implicit val i18ncfg = I18NConfig(
    UTF8Messages(Locale.US) orElse
    Messages(
      Locale.US,
      'custom_rule1 -> MessageFormat.Literal("custom_rule1"),
      'custom_rule2 -> MessageFormat.Literal("custom_rule2")
    )
  )

  val customValidator1_String = Validator.ensure[String](Rule('custom_rule1))(_ => true)
  val customValidator2_String = Validator.ensure[String](Rule('custom_rule2))(_ => true)

  implicit class StringVC(
    val underlying: String
    ) extends AnyVal with IsValueClass[String]
  object StringVC {
    implicit val validator_StringVC =
      Validator.forValueClass[StringVC, String] {
        import Validators._
        _ and StringLengthMin(1) and StringLengthMax(64) and AllLettersOrSpaces and customValidator1_String and customValidator2_String
      }
    implicit val format_StringVC =
      Json
        .forValueClass.format[StringVC,String]
        .withValidator

    implicit val explainPlayJson_StringVC =
      ExplainPlayJson.forValueClass[StringVC,String].withValidator
  }
  val jsonExplanationNode_StringVC = explainPlayJson[StringVC].value.asInstanceOf[JsonString]

  val customValidator1_Double = Validator.ensure[Double](Rule('custom_rule1))(_ => true)
  val customValidator2_Double = Validator.ensure[Double](Rule('custom_rule2))(_ => true)
  implicit class DoubleVC(
    val underlying: Double
    ) extends AnyVal with IsValueClass[Double]
  object DoubleVC {
    implicit val validator_DoubleVC =
      Validator.forValueClass[DoubleVC, Double] {
        import Validators._
        _ and NumberMinInclusive(1) and NumberMaxExclusive(100) and customValidator1_Double and customValidator2_Double
      }
    implicit val format_DoubleVC =
      Json
        .forValueClass.format[DoubleVC,Double]
        .withValidator

    implicit val explainPlayJson_DoubleVC =
      ExplainPlayJson.forValueClass[DoubleVC,Double].withValidator
  }
  val jsonExplanationNode_DoubleVC = explainPlayJson[DoubleVC].value.asInstanceOf[JsonNumber]


  val customValidator1_Int = Validator.ensure[Int](Rule('custom_rule1))(_ => true)
  val customValidator2_Int = Validator.ensure[Int](Rule('custom_rule2))(_ => true)
  implicit class IntVC(
    val underlying: Int
    ) extends AnyVal with IsValueClass[Int]
  object IntVC {
    implicit val validator_IntVC =
      Validator.forValueClass[IntVC, Int] {
        import Validators._
        _ and NumberMinExclusive(1) and NumberMaxInclusive(100) and customValidator1_Int and customValidator2_Int
      }
    implicit val format_IntVC =
      Json
        .forValueClass.format[IntVC,Int]
        .withValidator

    implicit val explainPlayJson_IntVC =
      ExplainPlayJson.forValueClass[IntVC,Int].withValidator
  }
  val jsonExplanationNode_IntVC = explainPlayJson[IntVC].value.asInstanceOf[JsonInteger]
  
  val customValidator1_Boolean = Validator.ensure[Boolean](Rule('custom_rule1))(_ => true)
  val customValidator2_Boolean = Validator.ensure[Boolean](Rule('custom_rule2))(_ => true)
  implicit class BooleanVC(
    val underlying: Boolean
    ) extends AnyVal with IsValueClass[Boolean]
  object BooleanVC {
    implicit val validator_BooleanVC =
      Validator.forValueClass[BooleanVC, Boolean] {
        _ and customValidator1_Boolean and customValidator2_Boolean
      }
    implicit val format_BooleanVC =
      Json
        .forValueClass.format[BooleanVC,Boolean]
        .withValidator

    implicit val explainPlayJson_BooleanVC =
      ExplainPlayJson.forValueClass[BooleanVC,Boolean].withValidator
  }
  val jsonExplanationNode_BooleanVC = explainPlayJson[BooleanVC].value.asInstanceOf[JsonBoolean]
  
}

class ExplainPlayJsonWithValidatorTest extends FlatSpec with Matchers {
  import ExplainPlayJsonWithValidatorTest._

  "ExplainPlayJsonOps.ruleToMaybeJsonRule" should "map supported validator rules to JSONSchema rules" in {
    import ValidatePlayJsonOps._

    ruleToMaybeJsonRule(
      Rules.StringLengthMin(1)
    ) should be(Some(
      JsonRule.StringMinLength(1)
    ))

    ruleToMaybeJsonRule(
      Rules.StringLengthMax(1)
    ) should be(Some(
      JsonRule.StringMaxLength(1)
    ))

    val regex = "^[A-Za-z]*$"
    ruleToMaybeJsonRule(
      Rules.StringPattern(regex)
    ) should be(Some(
      JsonRule.StringPattern(regex)
    ))

    ruleToMaybeJsonRule(
      Rules.NumberMinInclusive(1)
    ) should be(Some(
      JsonRule.Minimum(BigDecimal("1"),exclusive = false)
    ))

    ruleToMaybeJsonRule(
      Rules.NumberMinExclusive(1)
    ) should be(Some(
      JsonRule.Minimum(BigDecimal("1"),exclusive = true)
    ))

    ruleToMaybeJsonRule(
      Rules.NumberMaxInclusive(1)
    ) should be(Some(
      JsonRule.Maximum(BigDecimal("1"),exclusive = false)
    ))

    ruleToMaybeJsonRule(
      Rules.NumberMaxExclusive(1)
    ) should be(Some(
      JsonRule.Maximum(BigDecimal("1"),exclusive = true)
    ))


  }

  "ExplainPlayJsonOps.ruleToMaybeJsonRule" should "NOT map unsupported validator rules to JSONSchema rules" in {
    ValidatePlayJsonOps.ruleToMaybeJsonRule(
      Rule('customer_rule1,"1")
    ) should be(None)
  }

  "ExplainPlayJson.withValidator" should "add supported validation rules to the JSON explanation as JSONSchema rules" in {
    jsonExplanationNode_StringVC.rules should be(List(
      JsonRule.StringMinLength(1),
      JsonRule.StringMaxLength(64),
      JsonRule.StringPattern("^[A-Za-z ]*$")
    ))

    jsonExplanationNode_DoubleVC.rules should be(List(
      JsonRule.Minimum(1, exclusive = false),
      JsonRule.Maximum(100, exclusive = true)
    ))

    jsonExplanationNode_IntVC.rules should be(List(
      JsonRule.Minimum(1, exclusive = true),
      JsonRule.Maximum(100, exclusive = false)
    ))

    jsonExplanationNode_BooleanVC.rules should be(Nil)
  }

  "ExplainPlayJson.withValidator" should "add unsupported validation rules to the JSON explanation as additional rules" in {
    jsonExplanationNode_StringVC.additionalRules.map(_.apply(i18ncfg)) should be(List(
      "custom_rule1",
      "custom_rule2"
    ))
    jsonExplanationNode_DoubleVC.additionalRules.map(_.apply(i18ncfg)) should be(List(
      "custom_rule1",
      "custom_rule2"
    ))
    jsonExplanationNode_IntVC.additionalRules.map(_.apply(i18ncfg)) should be(List(
      "custom_rule1",
      "custom_rule2"
    ))
    jsonExplanationNode_BooleanVC.additionalRules.map(_.apply(i18ncfg)) should be(List(
      "custom_rule1",
      "custom_rule2"
    ))
  }

  "ExplainPlayJson.withValidator" should "throw UnsupportedOperationException for Option[A]" in {

  }
}
