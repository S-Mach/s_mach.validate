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
import s_mach.metadata.{Cardinality, Metadata}
import s_mach.string.CharGroup
import example.ExampleUsage._
import example.ExampleUsage2._
import s_mach.validate.example.ExampleI18N._
import s_mach.i18n.messages._

class ValidateTest extends FlatSpec with Matchers {

  "Validator.validate" should "return invalid for a incorrect string" in {
    Name("*" * 65).validate should equal(Invalid(Metadata.Val(
        Rule.StringLengthMax(64) :: Rule.StringCharGroupPattern(CharGroup.Letter,CharGroup.Space) :: Nil
    )))
  }

  "Validator.validate" should "return valid for a correct string" in {
    val v = Name("abc")
    v.validate should equal(Valid(v,Metadata.Val(Nil)))
  }

  "Validator.validate" should "return invalid for an incorrect number" in {
    WeightLb(1001).validate should equal(Invalid(Metadata.Val(
      Rule.NumberMaxExclusive(1000.0) :: Nil
    )))
  }

  "Validator.validate" should "return valid for a correct number" in {
    val v = WeightLb(500)
    v.validate should equal(Valid(v,Metadata.Val(Nil)))
  }

  val invalidPerson1 = Person(1001,Name("*" * 65),151)

  val expectedInvalidPerson1 = Metadata.Rec(
    Rule(m_age_plus_id_must_be_less_than_$n.bind(1000)) :: Nil,
    Seq(
      "id" -> Metadata.Val(Nil),
      "name" -> Metadata.Val(
        Rule.StringLengthMax(64) :: Rule.StringCharGroupPattern(CharGroup.Letter,CharGroup.Space) :: Nil
      ),
      "age" -> Metadata.Val(
        Rule.NumberMaxInclusive(150) :: Nil
      )
    )
  )
  val invalidPerson2 = Person(1,Name(""),1)
  val expectedInvalidPerson2 = Metadata.Rec(
    Nil,
    Seq(
      "id" -> Metadata.Val(Nil),
      "name" -> Metadata.Val(
        Rule.StringNonEmpty :: Nil
      ),
      "age" -> Metadata.Val(Nil)
    )
  )
  val validPerson = Person(1,Name("abc"),30)
  val expectedValidPerson = Metadata.Rec(
    Nil,
    Seq(
      "id" -> Metadata.Val(Nil),
      "name" -> Metadata.Val(Nil),
      "age" -> Metadata.Val(Nil)
    )
  )

  "Validator.validate" should "return invalid for an incorrect case class (1)" in {
    invalidPerson1.validate should equal(Invalid(
      expectedInvalidPerson1
    ))
  }

  "Validator.validate" should "return invalid for an incorrect case class (2)" in {
    invalidPerson2.validate should equal(Invalid(
      expectedInvalidPerson2
    ))
  }

  "Validator.validate" should "return valid for a correct case class" in {
    val v = validPerson
    v.validate should equal(Valid(v,expectedValidPerson))
  }

  "Validator.validate" should "return invalid for an incorrect case class (with nested case classes)" in {
    Family(
      invalidPerson1,
      invalidPerson2,
      Seq(invalidPerson1,invalidPerson2),
      Some(invalidPerson1),
      None
    ).validate should equal(Invalid(Metadata.Rec(
      Rule(m_father_must_be_older_than_children) ::
      Rule(m_mother_must_be_older_than_children) ::
      Nil,
      Seq(
        "father" -> expectedInvalidPerson1,
        "mother" -> expectedInvalidPerson2,
        "children" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrMore,
          Seq(
            expectedInvalidPerson1,
            expectedInvalidPerson2
          )
        ),
        "grandMother" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrOne,
          Seq(expectedInvalidPerson1)
        ),
        "grandFather" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrOne,
          Nil
        )
      )
    )))
  }

  "Validator.validate" should "return valid for a correct case class (with nested case classes)" in {
    val v = Family(
      // father & mother must be older than children
      validPerson.copy(age = 60),
      validPerson.copy(age = 60),
      Seq(validPerson,validPerson),
      Some(validPerson),
      None
    )

    v.validate should equal(Valid(v,Metadata.Rec(
      Nil,
      Seq(
        "father" -> expectedValidPerson,
        "mother" -> expectedValidPerson,
        "children" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrMore,
          Seq(
            expectedValidPerson,
            expectedValidPerson
          )
        ),
        "grandMother" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrOne,
          Seq(expectedValidPerson)
        ),
        "grandFather" -> Metadata.Arr(
          Nil,
          Cardinality.ZeroOrOne,
          Nil
        )
      )
    )))
  }

}
