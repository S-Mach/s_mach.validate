package s_mach.validate.play_json

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsSuccess, Json, JsError}
import s_mach.validate._
import org.scalatest.{FlatSpec, Matchers}


class PlayJsonUtilsTest extends FlatSpec with Matchers {
  "List[Rule].toOptJsError" should "return Some(JsError) when list contains a validation failure" in {
    val v = {
      import Text._
      nonEmpty and maxLength(64) and allDigits
    }

    v("!" * 65).toOptJsError should equal(Some(
      JsError("must not be longer than 64 characters") ++
      JsError("must contain only digits")
    ))
  }

  case class Test(i: Int)

  "withValidator" should "wrap existing Play Format and apply additional validation" in {
    implicit val v = Validator.ensure[Test]("must be less than 100")(_.i < 100)
    implicit val fmt = Json.format[Test].withValidator
    Json.fromJson[Test](Json.toJson(Test(1))) should equal(JsSuccess(Test(1),JsPath \ "i"))
    Json.fromJson[Test](Json.toJson(Test(101))) should equal(JsError(JsPath \ "i","must be less than 100"))
  }

  "withValidator" should "wrap existing Play Reads and apply additional validation" in {
    implicit val v = Validator.ensure[Test]("must be less than 100")(_.i < 100)
    implicit val w = Json.writes[Test]
    implicit val r = Json.reads[Test].withValidator
    Json.fromJson[Test](Json.toJson(Test(1))) should equal(JsSuccess(Test(1),JsPath \ "i"))
    Json.fromJson[Test](Json.toJson(Test(101))) should equal(JsError(JsPath \ "i","must be less than 100"))
  }
}
