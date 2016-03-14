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
package s_mach.validate.example

import org.scalatest.{FlatSpec, Matchers}
import s_mach.explain_json._
import s_mach.explain_play_json._
import s_mach.validate.example.ExampleUsage._
import s_mach.validate.example.ExampleUsage2._

class ExplainPlayJsonRemarksPrinterTest extends FlatSpec with Matchers {

  "ExplainPlayJson.printRemarks.print" should "correctly print remarks for a string validator" in {
    explainPlayJson[Name].printRemarks.print should equal(List(
      "this: must be string",
      "this: must not be empty",
      "this: must not be longer than 64 characters",
      "this: must contain only letters or spaces"
    ))
  }

  "ExplainPlayJson.printRemarks.print" should "correctly print remarks for a number validator" in {
    explainPlayJson[WeightLb].printRemarks.print should equal(List(
      "this: must be number",
      "this: must be greater than 0.0",
      "this: must be less than 1000.0"
    ))
  }

  "ExplainPlayJson.printRemarks.print" should "correctly print remarks for a single case class validator" in {
    explainPlayJson[Person].printRemarks.print should equal(List(
      "this: age plus id must be less than 1000",
      "id: must be integer",
      "name: must be string",
      "name: must not be empty",
      "name: must not be longer than 64 characters",
      "name: must contain only letters or spaces",
      "age: must be integer",
      "age: must be greater than or equal to 0",
      "age: must be less than or equal to 150"
    ))
  }

  "ExplainPlayJson.printRemarks.print" should "correctly print remarks for a nested case class validator" in {
    explainPlayJson[Family].printRemarks.print should equal(List(
      "this: father must be older than children",
      "this: mother must be older than children",
      "father: age plus id must be less than 1000",
      "father.id: must be integer",
      "father.name: must be string",
      "father.name: must not be empty",
      "father.name: must not be longer than 64 characters",
      "father.name: must contain only letters or spaces",
      "father.age: must be integer",
      "father.age: must be greater than or equal to 0",
      "father.age: must be less than or equal to 150",
      "mother: age plus id must be less than 1000",
      "mother.id: must be integer",
      "mother.name: must be string",
      "mother.name: must not be empty",
      "mother.name: must not be longer than 64 characters",
      "mother.name: must contain only letters or spaces",
      "mother.age: must be integer",
      "mother.age: must be greater than or equal to 0",
      "mother.age: must be less than or equal to 150",
      "children: must be array",
      "children[*]: age plus id must be less than 1000",
      "children[*]: must be integer",
      "children[*]: must be string",
      "children[*]: must not be empty",
      "children[*]: must not be longer than 64 characters",
      "children[*]: must contain only letters or spaces",
      "children[*]: must be integer",
      "children[*]: must be greater than or equal to 0",
      "children[*]: must be less than or equal to 150",
      "grandMother: optional",
      "grandMother: age plus id must be less than 1000",
      "grandMother.id: must be integer",
      "grandMother.name: must be string",
      "grandMother.name: must not be empty",
      "grandMother.name: must not be longer than 64 characters",
      "grandMother.name: must contain only letters or spaces",
      "grandMother.age: must be integer",
      "grandMother.age: must be greater than or equal to 0",
      "grandMother.age: must be less than or equal to 150",
      "grandFather: optional",
      "grandFather: age plus id must be less than 1000",
      "grandFather.id: must be integer",
      "grandFather.name: must be string",
      "grandFather.name: must not be empty",
      "grandFather.name: must not be longer than 64 characters",
      "grandFather.name: must contain only letters or spaces",
      "grandFather.age: must be integer",
      "grandFather.age: must be greater than or equal to 0",
      "grandFather.age: must be less than or equal to 150"
    ))
  }

}
