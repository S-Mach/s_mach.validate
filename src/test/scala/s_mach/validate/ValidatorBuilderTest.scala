package s_mach.validate

import org.scalatest.{Matchers, FlatSpec}
import ExampleUsage._

class ValidatorBuilderTest extends FlatSpec with Matchers {
  "ValidatorBuilder.build" should "automatically add a schema if it is missing" in {
    val v= Validator.builder[String].build()
    v.schema should equal(Schema(Nil,"java.lang.String",(1,1)) :: Nil)
  }
  "ValidatorBuilder.schema" should "set the schema for the builder (and not auto add a schema)" in {
    val s1 = Schema(Nil,"test1",(99,99))
    val s2 = Schema(Nil,"test2",(99,99))
    val v = Validator.builder[String]
      .schema(s1)
      .schema(s2)
      .build()
    v("asdf") should equal(Nil)
    v.schema should equal(s2 :: Nil)
  }
  "ValidatorBuilder.comment" should "add a comment rule to the validator" in {
    val v = Validator.builder[String].comment("comment").comment("otherComment").build()
    v("asdf") should equal(Nil)
    v.rules should equal(Rule(Nil,"comment") :: Rule(Nil,"otherComment") :: Nil)
  }
  "ValidatorBuilder.ensure(Validator)" should "add a validator to the validator being built" in {
    val v = Validator.builder[String].ensure(Text.nonEmpty).ensure(Text.allDigits).build()
    v("888") should equal(Nil)
    v("") should equal(Text.nonEmpty.rules)
    v("aaa") should equal(Text.allDigits.rules)
    v.rules should equal(Text.nonEmpty.rules ::: Text.allDigits.rules)
  }
  "ValidatorBuilder.ensure(message)(test)" should "add an EnsureValidator to the validator being built" in {
    val v = Validator.builder[String].ensure("non empty")(_.nonEmpty).ensure(Text.allDigits).build()
    v("888") should equal(Nil)
    v("") should equal(Rule(Nil,"non empty") :: Nil)
    v.rules should equal(Rule(Nil,"non empty") :: Text.allDigits.rules)
  }
  "ValidatorBuilder.field" should "add a FieldValidator for a field to the validator being built" in {
    val v = Validator.builder[Person].field("age",_.age)(_.ensure("test")(_ < 100)).build()
    v(Person(1,"",99)) should equal(Nil)
    v(Person(1,"",101)) should equal(Rule("age" :: Nil,"test") :: Nil)
    v.rules should equal(Rule("age" :: Nil,"test") :: Nil)
    v.schema should equal(
      Schema(Nil,"s_mach.validate.ExampleUsage$Person",(1,1)) ::
      Schema("age" :: Nil,"Int",(1,1)) ::
    Nil
    )
  }
}
