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
package s_mach.validate.play_json

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json._
import s_mach.validate._
import s_mach.validate.play_json.example._
import s_mach.validate.MessageForRule.Implicits._

class ValidatorResultsJsonPrinterTest extends FlatSpec with Matchers {
  import ExampleUsage._
  import ExampleUsage2._

  "Validator.validate.printJs" should "correctly print JSON for a string validator" in {
    Name("*" * 65).validate.printJs.pretty should equal(
"""[ "must not be longer than 64 characters", "must contain only letters or spaces" ]"""
    )
  }

  "Validator.validate.printJs" should "correctly print JSON for a number validator" in {
    WeightLb(1001).validate.printJs.pretty should equal(
"""[ "must be less than 1000.0" ]"""
    )
  }

  val invalidPerson1 = Person(1001,Name("*" * 65),151)
  val invalidPerson2 = Person(1,Name(""),1)

  "Validator.validate.printJs" should "correctly print JSON for a single case class validator" in {
    invalidPerson1.validate.printJs.pretty should equal(
"""{
  "this" : [ "age plus id must be less than 1000" ],
  "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
  "age" : [ "must be less than or equal to 150" ]
}"""
    )
  }

  "Validator.validate.printJs" should "correctly print JSON for a nested case class validator" in {
    Family(
      invalidPerson1,
      invalidPerson2,
      Seq(invalidPerson1,invalidPerson2),
      Some(invalidPerson1),
      None
    ).validate.printJs.pretty should equal(
"""{
  "this" : [ "father must be older than children", "mother must be older than children" ],
  "father" : {
    "this" : [ "age plus id must be less than 1000" ],
    "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be less than or equal to 150" ]
  },
  "mother" : {
    "name" : [ "must not be empty" ]
  },
  "children" : {
    "0" : {
      "this" : [ "age plus id must be less than 1000" ],
      "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
      "age" : [ "must be less than or equal to 150" ]
    },
    "1" : {
      "name" : [ "must not be empty" ]
    }
  },
  "grandMother" : {
    "this" : [ "age plus id must be less than 1000" ],
    "name" : [ "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be less than or equal to 150" ]
  }
}"""
    )
  }

}
