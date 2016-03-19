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
       ;it1i ,,,:::;;;::1tti      s_mach.validate
         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

import org.scalatest.{FlatSpec, Matchers}
import s_mach.codetools.play_json._
import s_mach.explain_json._
import s_mach.explain_play_json._
import s_mach.explain_play_json.PlayJsonWriter.Implicits._
import s_mach.metadata.Metadata
import s_mach.string.CharGroup
import s_mach.validate.play_json._
import s_mach.validate.MessageForRule.Implicits._
import example.ExampleUsage._
import example.ExampleUsage2._
import Validators._

class ValidateTest extends FlatSpec with Matchers {

  "Validator.validate" should "return invalid for a incorrect string" in {
    Name("*" * 65).validate should equal(Invalid(Metadata.Val(
        stringLengthMax.rule(64) :: stringCharGroupPattern.rule(CharGroup.Letter,CharGroup.Space) :: Nil
    )))
  }

  "Validator.validate" should "return valid for a correct string" in {
    val v = Name("abc")
    v.validate should equal(Valid(v,Metadata.Val(Nil)))
  }

  "Validator.validate" should "return invalid for an incorrect number" in {
    WeightLb(1001).validate should equal(Invalid(Metadata.Val(
      numberMaxExclusive.rule(1000.0) :: Nil
    )))
  }

  "Validator.validate" should "return valid for a correct number" in {
    val v = WeightLb(500)
    v.validate should equal(Valid(v,Metadata.Val(Nil)))
  }

  val invalidPerson1 = Person(1001,Name("*" * 65),151)
  val invalidPerson2 = Person(1,Name(""),1)

  "Validator.validate" should "return invalid for an incorrect case class" in {
    invalidPerson1.validate should equal(Invalid(Metadata.Rec(
      Rule("age plus id must be less than 1000") :: Nil,
      Seq(
        "id" -> Metadata.Val(Nil),
        "name" -> Metadata.Val(
          stringLengthMax.rule(64) :: stringCharGroupPattern.rule(CharGroup.Letter,CharGroup.Space) :: Nil
        ),
        "age" -> Metadata.Val(
          numberMaxInclusive.rule(150) :: Nil
        )
      )
    )))
  }

  "Validator.validate" should "return valid for a correct case class" in {
    val v = Person(1,Name("abc"),30)
    v.validate should equal(Valid(v,Metadata.Rec(
      Nil,
      Seq(
        "id" -> Metadata.Val(Nil),
        "name" -> Metadata.Val(Nil),
        "age" -> Metadata.Val(Nil)
      )
    )))
  }

  //  "Validator.validate" should "return invalid for an incorrect case class (with nested case classes)" in {
//    Family(
//      invalidPerson1,
//      invalidPerson2,
//      Seq(invalidPerson1,invalidPerson2),
//      Some(invalidPerson1),
//      None
//    ).validate should equal(Invalid(Metadata.Rec(
//      Rule("father must be older than children") :: Rule("mother must be older than children") :: Nil,
//      Seq(
//        "father" -> Metadata.Rec(
//          Rule("age plus id must be less than 1000") :: Nil,
//          Seq(
//            "id" -> Metadata.Val(Nil),
//            "name" -> Metadata.Val(
//              stringLengthMax.rule(64) :: stringCharGroupPattern.rule(CharGroup.Letter,CharGroup.Space) :: Nil
//            ),
//            "age" -> Metadata.Val(
//              numberMaxInclusive.rule(150) :: Nil
//            )
//          )
//        ),
//        "mother" -> Metadata.Rec(
//          Rule("age plus id must be less than 1000") :: Nil,
//          Seq(
//            "name" -> Metadata.Val(
//              stringNonEmpty.rule() :: Nil
//            )
//          )
//        ),
//        "children" -> Metadata.Rec(
//          Rule("age plus id must be less than 1000") :: Nil,
//          Seq(
//            "id" -> Metadata.Val(Nil),
//            "name" -> Metadata.Val(
//              stringLengthMax.rule(64) :: stringCharGroupPattern.rule(CharGroup.Letter,CharGroup.Space) :: Nil
//            ),
//            "age" -> Metadata.Val(
//              numberMaxInclusive.rule(150) :: Nil
//            )
//          )
//        ),
//        "grandMother" -> Metadata.Rec(
//          Rule("age plus id must be less than 1000") :: Nil,
//          Seq(
//            "id" -> Metadata.Val(Nil),
//            "name" -> Metadata.Val(
//              stringLengthMax.rule(64) :: stringCharGroupPattern.rule(CharGroup.Letter,CharGroup.Space) :: Nil
//            ),
//            "age" -> Metadata.Val(
//              numberMaxInclusive.rule(150) :: Nil
//            )
//          )
//        )
//      )
//    )))
//  }

//  "Validator.validate.printJs" should "correctly print JSON for a nested case class validator" in {
//"""{
//  "this" : [ "father must be older than children", "mother must be older than children" ],
//  "father" : {
//    "this" : [ "age plus id must be less than 1000" ],
//    "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
//    "age" : [ "must be less than or equal to 150" ]
//  },
//  "mother" : {
//    "name" : [ "must not be empty" ]
//  },
//  "children" : {
//    "0" : {
//      "this" : [ "age plus id must be less than 1000" ],
//      "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
//      "age" : [ "must be less than or equal to 150" ]
//    },
//    "1" : {
//      "name" : [ "must not be empty" ]
//    }
//  },
//  "grandMother" : {
//    "this" : [ "age plus id must be less than 1000" ],
//    "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
//    "age" : [ "must be less than or equal to 150" ]
//  }
//}"""
//    )
//  }

}
