/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      s_mach.validate.play_json
         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.play_json

import java.util.Locale

import org.scalatest.{FlatSpec, Matchers}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import s_mach.validate.{Rule, Validator}
import s_mach.i18n.I18NConfig
import s_mach.i18n.messages.{MessageFormat, Messages}

object FormatWithValidatorTest {
  implicit val i18ncfg = I18NConfig(
    Messages(
      Locale.US,
      'rule1 -> MessageFormat.Literal("age must be less than or equal to 150"),
      'rule2 -> MessageFormat.Literal("name must contain only letters or spaces"),
      'rule3 -> MessageFormat.Literal("name must not be empty")
    )
  )

  case class Person(
    id: Long,
    name: String,
    age: Int
  )
  implicit val validator_Person =
      Validator.forProductType[Person]
        .ensure(Rule('rule1))(_.age <= 150)
        .ensure(Rule('rule2))(_.name.forall(c => c.isLetterOrDigit || c == ' '))
        .ensure(Rule('rule3))(_.name.nonEmpty)

  implicit val format_Person = Json.format[Person].withValidator
}
class FormatWithValidatorTest extends FlatSpec with Matchers {
  import FormatWithValidatorTest._


  "Format.withValidator" should "wrap Format in a wrapper that applies Validator rules" in {
    val p1 = Person(1,"!!!",200)
    val json1 = Json.toJson(p1)
    Json.fromJson[Person](json1) should equal(JsError(Stream(
      (JsPath,List(
        ValidationError("age must be less than or equal to 150"),
        ValidationError("name must contain only letters or spaces")
      ))
    )))

    val p2 = Person(1,"",100)
    val json2 = Json.toJson(p2)
    Json.fromJson[Person](json2) should equal(JsError(List(
      (JsPath,List(ValidationError("name must not be empty")))
    )))
  }

  "Format.withValidator" should "pass valid data" in {
    val p = Person(1,"asdf",149)
    val json = Json.toJson(p)
    Json.fromJson[Person](json) should equal(JsSuccess(p))
  }

  "Format.withValidator" should "fail normally with bad json" in {
    val json = Json.obj("id" -> 1,"age" -> 149)
    Json.fromJson[Person](json) should equal(JsError(List(
      (JsPath \ "name",List(ValidationError("error.path.missing")))
    )))
  }
}
