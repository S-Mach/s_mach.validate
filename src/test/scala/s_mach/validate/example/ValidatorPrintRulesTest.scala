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
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.example

import s_mach.validate._
import org.scalatest.{FlatSpec, Matchers}
import s_mach.validate.MessageForRule.Implicits._

class ValidatorPrintRulesTest extends FlatSpec with Matchers {
  import example.ExampleUsage._
  import example.ExampleUsage2._

  "Validator.rules.printRemarks" should "correctly print type remarks for a string validator" in {
    validator[Name].rules.printRemarks should equal(List(
      "this: must not be empty",
      "this: must not be longer than 64 characters",
      "this: must contain only letters or spaces"
    ))
  }

  "Validator.rules.printRemarks" should "correctly print type remarks for a number validator" in {
    validator[WeightLb].rules.toTypeRemarks.print should equal(List(
      "this: must be greater than 0.0",
      "this: must be less than 1000.0"
    ))
  }

  "Validator.rules.printRemarks" should "correctly print type remarks for a single case class validator" in {
    validator[Person].rules.toTypeRemarks.print should equal(List(
      "this: age plus id must be less than 1000",
      "name: must not be empty",
      "name: must not be longer than 64 characters",
      "name: must contain only letters or spaces",
      "age: must be greater than or equal to 0",
      "age: must be less than or equal to 150"
    ))
  }

  "Validator.rules.printRemarks" should "correctly print type remarks for a nested case class validator" in {
    validator[Family].rules.toTypeRemarks.print should equal(List(
      "this: father must be older than children",
      "this: mother must be older than children",
      "father: age plus id must be less than 1000",
      "father.name: must not be empty",
      "father.name: must not be longer than 64 characters",
      "father.name: must contain only letters or spaces",
      "father.age: must be greater than or equal to 0",
      "father.age: must be less than or equal to 150",
      "mother: age plus id must be less than 1000",
      "mother.name: must not be empty",
      "mother.name: must not be longer than 64 characters",
      "mother.name: must contain only letters or spaces",
      "mother.age: must be greater than or equal to 0",
      "mother.age: must be less than or equal to 150",
      "children[*]: age plus id must be less than 1000",
      "children[*]: must not be empty",
      "children[*]: must not be longer than 64 characters",
      "children[*]: must contain only letters or spaces",
      "children[*]: must be greater than or equal to 0",
      "children[*]: must be less than or equal to 150",
      "grandMother: age plus id must be less than 1000",
      "grandMother.name: must not be empty",
      "grandMother.name: must not be longer than 64 characters",
      "grandMother.name: must contain only letters or spaces",
      "grandMother.age: must be greater than or equal to 0",
      "grandMother.age: must be less than or equal to 150",
      "grandFather: age plus id must be less than 1000",
      "grandFather.name: must not be empty",
      "grandFather.name: must not be longer than 64 characters",
      "grandFather.name: must contain only letters or spaces",
      "grandFather.age: must be greater than or equal to 0",
      "grandFather.age: must be less than or equal to 150"
    ))
  }

}
