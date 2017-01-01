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
package s_mach.validate

import org.scalatest.{FlatSpec, Matchers}
import s_mach.codetools._
import s_mach.validate.impl._
import s_mach.i18n.messages._

object ValidatorAndTest {
  object DTA {
    trait AgeTag
    type Age = Int with AgeTag with IsDistinctTypeAlias[Int]
  }
  object ValueClass {
    implicit class Age(val underlying: Int) extends AnyVal with IsValueClass[Int]
  }
  val m_test1 = 'test1.literal
  val m_test2 = 'test2.literal
  val m_comment1 = 'comment1.literal
  val m_comment2 = 'comment2.literal

  val testRule1 = Rule(m_test1)
  val testRule2 = Rule(m_test2)
  val testCommentRule1 = Rule(m_comment1)
  val testCommentRule2 = Rule(m_comment2)

  val m_number_between = 'number_between.m[Int,Int]

  // Note: don't copy these fake checks for testing only
  val c1 = Validator.ensure[Int](
    Rule(m_number_between.bind(0,150))
  )(age => 0 <= age && age <= 150).asInstanceOf[CheckValidator[Int]]
  val c2 = Validator.ensure[Int](
    Rule(m_number_between.bind(10,70))
  )(age => 10 <= age && age <= 70).asInstanceOf[CheckValidator[Int]]

}
class ValidatorAndTest extends FlatSpec with Matchers {
  import ValidatorAndTest._

  "Empty Val Validator and some other validator" should "be the other validator" in {
    val v1 = Validator.forVal[Int]()
    val v2 = Validator.ensure[Int](testRule1)(_ > 100)
    v1 and v2 should equal(v2)
  }

  "CheckValidator and CheckValidator" should "combine into a single CheckValidator" in {
    val v1 = Validator.ensure[Int](testRule1)(_ > 100).asInstanceOf[CheckValidator[Int]]
    val v2 = Validator.ensure[Int](testRule2)(_ > 101).asInstanceOf[CheckValidator[Int]]
    val v3 = (v1 and v2).asInstanceOf[CheckValidator[Int]]
    v3.checks should equal(v1.checks ::: v2.checks)
  }

  "Combining two unlike validators" should "results in a CompositeValidator" in {
    val v1 = Validator.ensure[Int](testRule1)(_ > 100)
    val v2 = Validator.comment[Int](testCommentRule1)
    val v3 = (v1 and v2).asInstanceOf[CompositeValidator[Int]]
    v3.validators should equal(v1 :: v2 :: Nil)

  }
  "CompositeValidator and CompositeValidator" should "combine into a single CompositeValidator" in {
    val v1 = (Validator.ensure[Int](testRule1)(_ > 100) and Validator.comment(testCommentRule1)).asInstanceOf[CompositeValidator[Int]]
    val v2 = (Validator.ensure[Int](testRule2)(_ > 100) and Validator.comment(testCommentRule2)).asInstanceOf[CompositeValidator[Int]]
    val v3 = (v1 and v2).asInstanceOf[CompositeValidator[Int]]
    v3.validators should equal(v1.validators ::: v2.validators)
  }

  "DistinctTypeAliasValidator and DistinctTypeAliasValidator" should "combine into a single DistinctTypeAliasValidator" in {
    import DTA._

    implicit val validator_Int = Validator.forVal[Int]()
    val v1 = Validator.forDistinctTypeAlias[Age,Int](_ and c1).asInstanceOf[DistinctTypeAliasValidator[Age,Int]]
    val v2 = Validator.forDistinctTypeAlias[Age,Int](_ and c2).asInstanceOf[DistinctTypeAliasValidator[Age,Int]]

    val v3 = (v1 and v2).asInstanceOf[DistinctTypeAliasValidator[Age,Int]]
    v3.va.asInstanceOf[CheckValidator[Int]].checks should equal(c1.checks ::: c2.checks)
  }

  "ValueClassValidator and ValueClassValidator" should "combine into a single ValueClassValidator" in {
    import ValueClass._

    implicit val validator_Int = Validator.forVal[Int]()
    val v1 = Validator.forValueClass[Age,Int](_ and c1).asInstanceOf[ValueClassValidator[Age,Int]]
    val v2 = Validator.forValueClass[Age,Int](_ and c2).asInstanceOf[ValueClassValidator[Age,Int]]

    val v3 = (v1 and v2).asInstanceOf[ValueClassValidator[Age,Int]]
    v3.va.asInstanceOf[CheckValidator[Int]].checks should equal(c1.checks ::: c2.checks)
  }

  "OptionValidator and OptionValidator" should "combine into a single OptionValidator" in {
    val v1 = Validator.forOption[Int](c1).asInstanceOf[OptionValidator[Int]]
    val v2 = Validator.forOption[Int](c2).asInstanceOf[OptionValidator[Int]]
    val v3 = (v1 and v2).asInstanceOf[OptionValidator[Int]]
    v3.va.asInstanceOf[CheckValidator[Int]].checks should equal(c1.checks ::: c2.checks)
  }

  "TraversableValidator and TraversableValidator" should "combine into a single TraversableValidator" in {
    val v1 = Validator.forTraversable[Seq,Int](c1).asInstanceOf[TraversableValidator[Seq,Int]]
    val v2 = Validator.forTraversable[Seq,Int](c2).asInstanceOf[TraversableValidator[Seq,Int]]
    val v3 = (v1 and v2).asInstanceOf[TraversableValidator[Seq,Int]]
    v3.va.asInstanceOf[CheckValidator[Int]].checks should equal(c1.checks ::: c2.checks)
  }

}
