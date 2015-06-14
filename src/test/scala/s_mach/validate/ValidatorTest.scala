package s_mach.validate

import org.scalatest.{FlatSpec, Matchers}
import scala.reflect._
import ExampleUsage._

class ValidatorTest extends FlatSpec with Matchers {
  "Validator.ensure" should "create a validator with a single rule and no schema" in {
    val v = Validator.ensure[String]("message")(_ == "test")
    v("test") should equal(Nil)
    val expected = Rule(Nil,"message") :: Nil
    v("nottest") should equal(expected)
    v.rules should equal(expected)
    v.schema should equal(Schema(Nil,"java.lang.String",(1,1)))
  }
  "Validator.apply" should "create a validator composed of other validators" in {
    val v = Validator(Text.nonEmpty,Text.allDigits)
    v("999") should equal(Nil)
    v("") should equal(Text.nonEmpty.rules)
    v("aaa") should equal(Text.allDigits.rules)
    v.rules should equal(Text.nonEmpty.rules ::: Text.allDigits.rules)
    v.schema should equal(Schema(Nil,"java.lang.String",(1,1)))
  }
  "Validator.comment" should "create a validator that contains a rule that is never checked" in {
    val v = Validator.comment[String]("message")
    v("999") should equal(Nil)
    v("") should equal(Nil)
    v("aaa") should equal(Nil)
    v.rules should equal(Rule(Nil,"message") :: Nil)
    v.schema should equal(Schema(Nil,"java.lang.String",(1,1)))
  }
  "Validator.optional" should "create a validator that modifies the cardinality of another validator" in {
    val v2 = Text.nonEmpty and Text.allDigits
    val v = Validator.optional(v2)


    v(Some("999")) should equal(Nil)
    v(None) should equal(Nil)
    v(Some("")) should equal(Text.nonEmpty.rules)
    v(Some("aaa")) should equal(Text.allDigits.rules)
    v.rules should equal(Text.nonEmpty.rules ::: Text.allDigits.rules)
    v.schema should equal(Schema(Nil,"java.lang.String",(0,1)))
  }

  "Validator.zeroOrMore" should "create a validator with zero or more cardinality that wraps another validator" in {
    val v2 = Text.nonEmpty and Text.allDigits
    val v : Validator[Vector[String]] = Validator.zeroOrMore(v2)

    v(Vector("999")) should equal(Nil)
    v(Vector.empty) should equal(Nil)
    v(Vector("999","")) should equal(Text.nonEmpty.rules.map(_.pushPath("1")))
    v(Vector("999","aaa")) should equal(Text.allDigits.rules.map(_.pushPath("1")))
    v.rules should equal(Text.nonEmpty.rules ::: Text.allDigits.rules)
    v.schema should equal(Schema(Nil,"java.lang.String",(0,Int.MaxValue)))
  }
  "Validator implicits" should "create optional and zero or more validators for existing implicit validators" in {
    val ov = validator[Option[Name]]
    ov(Some("aaa")) should equal(Nil)
    ov(None) should equal(Nil)
    ov(Some("")) should equal(Text.nonEmpty.rules)
    ov(Some("999")) should equal(Text.allLettersOrSpaces.rules)
    ov.rules should equal(validator[Name].rules)
    ov.schema should equal(Schema(Nil,"java.lang.String",(0,1)))

    val zv = validator[Vector[Name]]
    zv(Vector("aaa")) should equal(Nil)
    zv(Vector.empty) should equal(Nil)
    zv(Vector("aaa","")) should equal(Text.nonEmpty.rules.map(_.pushPath("1")))
    zv(Vector("aaa","999")) should equal(Text.allLettersOrSpaces.rules.map(_.pushPath("1")))
    zv.rules should equal(validator[Name].rules)
    zv.schema should equal(Schema(Nil,"java.lang.String",(0,Int.MaxValue)))
  }
}
