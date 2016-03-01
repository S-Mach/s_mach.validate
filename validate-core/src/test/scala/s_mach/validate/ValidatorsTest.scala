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

import org.scalatest.{Matchers, FlatSpec}
import s_mach.metadata._
import Validators._

import scala.collection.immutable.NumericRange
import scala.util.Random

class ValidatorsTest extends FlatSpec with Matchers {
  val TEST_COUNT = 1000


  val noFail = Metadata.Val(Nil)

  "stringLengthMin" should "should reject strings with length less than a certain length" in {
    val v = stringLengthMin(64)
    val failed = Metadata.Val(v.thisRules)
    // inclusive seam
    v("1" * 63) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(Random.nextString(n)),n) should equal(
        (failed,n)
      )
    }
  }

  "stringLengthMin" should "should accept strings with that are at longer than a certain length" in {
    val v = stringLengthMin(64)
    // inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 64
      (v(Random.nextString(n)),n) should equal((noFail,n))
    }

  }

  "stringLengthMax" should "should reject strings with length longer than a certain length" in {
    val v = stringLengthMax(64)
    val failed = Metadata.Val(v.thisRules)
    // inclusive seam
    v("1" * 65) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 65
      (v(Random.nextString(n)),n) should equal(
        (failed,n)
      )
    }
  }

  "stringLengthMax" should "should accept strings with that are no longer than a certain length" in {
    val v = stringLengthMax(64)
    // inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(Random.nextString(n)),n) should equal((noFail,n))
    }
  }


  "stringLengthRange" should "should reject strings with that are not between a certain range" in {
    val v = stringLengthRange(20,64)
    val minFailed = Metadata.Val(stringLengthMin(20).thisRules)
    val maxFailed = Metadata.Val(stringLengthMax(64).thisRules)
    // min inclusive seam
    v("1" * 19) should equal(minFailed)
    // max inclusive seam
    v("1" * 65) should equal(maxFailed)
    for(i <- 0 until TEST_COUNT) {
      val n = {
        val n1 = Random.nextInt(128)
        if (n1 >= 20) n1 + (64-20) + 1 else n1
      }
      v(Random.nextString(n)) should equal(if(n < 20) minFailed else maxFailed)
    }
  }

  "stringLengthRange" should "should accept strings with that are between a certain range" in {
    val v = stringLengthRange(20,64)
    // min inclusive seam
    v("1" * 20) should equal(noFail)
    // max inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt((63-20))+20
      (n,v(Random.nextString(n))) should equal((n,noFail))
    }
  }

  "stringNonEmpty" should "test the same rules as stringLengthMin(1)" in {
    stringNonEmpty.thisRules == stringLengthMin(1).thisRules
  }

  def mkStringGen(length: Range, chars: Array[Char]) : () => String = {
    def charGen = chars(Random.nextInt(chars.length))

    { () =>
      val n = Random.nextInt(length.length) + length.head
      Array.fill(n)(charGen).mkString
    }
  }

  def mkStringGen(length: Range, chars: NumericRange.Inclusive[Char]*) : () => String = {
    mkStringGen(length, chars.flatMap(_.map(_.toChar)).toArray)
  }


  val allChars = {
    val ranges =
      Seq(
        'a' to 'z',
        'A' to 'Z',
        '0' to '9'
      )
    val singles = Seq(
      ' ','~','`','!','@','#','$','%','^','&','*','(',')','-','_','+','=',
      '[','{',']','}','\\','|',';',':','\'','"',',','<','.','>','?','/'
    )
    ranges.flatten ++ singles
  }.toArray

  def testPatternValidator(
    v: Validator[String],
    length: Range,
    chars: NumericRange.Inclusive[Char]*
  ) = {
    val passChars = chars.flatten.toArray
    val passStringGen = mkStringGen(length,passChars)
    val failChars = allChars.filterNot(c => passChars.contains(c))
    require(failChars.length > 0)
    val failStringGen = mkStringGen(length, failChars)
    for(i <- 0 until TEST_COUNT) {
      val s = passStringGen()
      (s,v(s)) should equal((s,noFail))
    }
    val failed = Metadata.Val(v.thisRules)
    for(i <- 0 until TEST_COUNT) {
      val s = failStringGen()
      (s,v(s)) should equal((s,failed))
    }
  }

  "stringPattern" should "accept strings that match a regex pattern and reject regex that do not " in {
    val v = stringPattern("^[A-Z0-9]{10,24}$")
    testPatternValidator(v,10 to 24,'A' to 'Z','0' to '9')
  }

  "allLetters" should "accept strings of all letters" in {
    testPatternValidator(allLetters,10 to 24,'A' to 'Z','a' to 'z')
  }

  "allDigits" should "accept strings of all digits" in {
    testPatternValidator(allDigits,10 to 24,'0' to '9')
  }

  "allLettersOrDigits" should "accept strings of all letters or digits" in {
    testPatternValidator(allLettersOrDigits,10 to 24,'A' to 'Z','a' to 'z','0' to '9')
  }

  "allLettersOrSpaces" should "accept strings of all letters or spaces" in {
    testPatternValidator(allLettersOrSpaces,10 to 24,'A' to 'Z',' ' to ' ')
  }

  "allLettersDigitsOrSpaces" should "accept strings of all letters, digits or spaces" in {
    testPatternValidator(allLettersDigitsOrSpaces,10 to 24,'A' to 'Z','0' to '9',' ' to ' ')
  }

  "numberMinInclusive" should "reject numbers less than a certain value" in {
    val v = numberMinInclusive(64)
    val failed = Metadata.Val(v.thisRules)
    // inclusive seam
    v(63) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "numberMinInclusive" should "should accept numbers greater than or equal to a certain value" in {
    val v = numberMinInclusive(64)
    // inclusive seam
    v(64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 64
      (v(n),n) should equal((noFail,n))
    }
  }

  "numberMaxInclusive" should "reject numbers greater than a certain value" in {
    val v = numberMaxInclusive(64)
    val failed = Metadata.Val(v.thisRules)
    // inclusive seam
    v(65) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63) + 65
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "numberMaxInclusive" should "should accept numbers less than or equal to a certain value" in {
    val v = numberMaxInclusive(64)
    // inclusive seam
    v(64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(64)
      (v(n),n) should equal((noFail,n))
    }

  }

  "numberMinExclusive" should "reject numbers below or equal to a certain value" in {
    val v = numberMinExclusive(64)
    val failed = Metadata.Val(v.thisRules)
    // Exclusive seam
    v(64) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "numberMinExclusive" should "should accept numbers greater than a certain value" in {
    val v = numberMinExclusive(64)
    // Exclusive seam
    v(65) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 65
      (v(n),n) should equal((noFail,n))
    }

  }

  "numberMaxExclusive" should "reject numbers greater than or equal to a certain value" in {
    val v = numberMaxExclusive(64)
    val failed = Metadata.Val(v.thisRules)
    // Exclusive seam
    v(64) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63) + 64
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "numberMaxExclusive" should "should accept less than a certain value" in {
    val v = numberMaxExclusive(64)
    // Exclusive seam
    v(63) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal((noFail,n))
    }

  }
  
  "numberRange" should "should accept numbers between a certain range (inclusive)" in {
    val v = numberRange(20,64)
    val minFailed = Metadata.Val(numberMinInclusive(20).thisRules)
    val maxFailed = Metadata.Val(numberMaxInclusive(64).thisRules)
    // min inclusive seam
    v(20) should equal(noFail)
    v(19) should equal(minFailed)
    // max inclusive seam
    v(64) should equal(noFail)
    v(65) should equal(maxFailed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(128)
      (n,v(n)) should equal(
        if(n < 20) (n,minFailed) else if(n > 64) (n,maxFailed) else (n,noFail)
      )
    }

  }
}
