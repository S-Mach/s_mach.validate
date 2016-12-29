///*
//                    ,i::,
//               :;;;;;;;
//              ;:,,::;.
//            1ft1;::;1tL
//              t1;::;1,
//               :;::;               _____       __  ___              __
//          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
//         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
//         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
//       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
//       Lft1,:;:       , 1tfL:
//       ;it1i ,,,:::;;;::1tti      s_mach.validate.play_json
//         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
//         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
//          .L1 1tt1ttt,,Li
//            ...1LLLL...
//*/
//package s_mach.validate_play_json
//
//import org.scalatest.{Matchers, FlatSpec}
//import play.api.data.validation.ValidationError
//import play.api.libs.json._
//
//class FormatWithValidatorTest extends FlatSpec with Matchers {
//  import example.ExampleUsage._
//
//  "Format.withValidator" should "wrap Format in a wrapper that applies Validator rules" in {
//    val p1 = Person(1,"!!!",200)
//    val json1 = Json.toJson(p1)
//    Json.fromJson[Person](json1) should equal(JsError(List(
//      (JsPath \ "age",List(ValidationError("must be less than or equal to 150"))),
//      (JsPath \ "name",List(ValidationError("must contain only letters or spaces")))
//    )))
//
//    val p2 = Person(1,"",100)
//    val json2 = Json.toJson(p2)
//    Json.fromJson[Person](json2) should equal(JsError(List(
//      (JsPath \ "name",List(ValidationError("must not be empty")))
//    )))
//  }
//
//  "Format.withValidator" should "pass valid data" in {
//    val p = Person(1,"asdf",149)
//    val json = Json.toJson(p)
//    Json.fromJson[Person](json) should equal(JsSuccess(p))
//  }
//
//  "Format.withValidator" should "fail normally with bad json" in {
//    val json = Json.obj("id" -> 1,"age" -> 149)
//    Json.fromJson[Person](json) should equal(JsError(List(
//      (JsPath \ "name",List(ValidationError("error.path.missing")))
//    )))
//  }
//}
