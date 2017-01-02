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

import scala.collection.immutable.NumericRange
import scala.util.Random

class ValidatorsTest extends FlatSpec with Matchers {
  val TEST_COUNT = 1000


  val noFail = Stream.empty

  "Validators.StringLengthMin" should "should reject strings with length less than a certain length" in {
    val v = Validators.StringLengthMin(64)
    val failed = v.thisRules.toStream.map((Nil,_))
    // inclusive seam
    v("1" * 63) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(Random.nextString(n)),n) should equal(
        (failed,n)
      )
    }
  }

  "Validators.StringLengthMin" should "should accept strings with that are at longer than a certain length" in {
    val v = Validators.StringLengthMin(64)
    // inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 64
      (v(Random.nextString(n)),n) should equal((noFail,n))
    }

  }

  "Validators.StringLengthMax" should "should reject strings with length longer than a certain length" in {
    val v = Validators.StringLengthMax(64)
    val failed = v.thisRules.toStream.map((Nil,_))
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
    val v = Validators.StringLengthMax(64)
    // inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(Random.nextString(n)),n) should equal((noFail,n))
    }
  }


  "Validators.StringLengthRange" should "should reject strings with that are not between a certain range" in {
    val v = Validators.StringLengthRange(20,64)
    val failMin = Stream((Nil,Rule.StringLengthMin(20)))
    val failMax = Stream((Nil,Rule.StringLengthMax(64)))
    // min inclusive seam
    v("1" * 19) should equal(failMin)
    // max inclusive seam
    v("1" * 65) should equal(failMax)
    for(i <- 0 until TEST_COUNT) {
      val n = {
        val n1 = Random.nextInt(128)
        if (n1 >= 20) n1 + (64-20) + 1 else n1
      }
      v(Random.nextString(n)) should equal(if(n < 20) failMin else failMax)
    }
  }

  "Validators.StringLengthRange" should "should accept strings with that are between a certain range" in {
    val v = Validators.StringLengthRange(20,64)
    // min inclusive seam
    v("1" * 20) should equal(noFail)
    // max inclusive seam
    v("1" * 64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt((63-20))+20
      (n,v(Random.nextString(n))) should equal((n,noFail))
    }
  }

//  "stringNonEmpty" should "test the same rules as stringLengthMin(1)" in {
//    stringNonEmpty().thisRules == stringLengthMin(1).thisRules
//  }

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
    val failed = v.thisRules.toStream.map((Nil,_))
    for(i <- 0 until TEST_COUNT) {
      val s = failStringGen()
      (s,v(s)) should equal((s,failed))
    }
  }

  "Validators.stringPattern" should "accept strings that match a regex pattern and reject regex that do not " in {
    val v = Validators.StringPattern("^[A-Z0-9]{10,24}$")
    testPatternValidator(v,10 to 24,'A' to 'Z','0' to '9')
  }

  "Validators.allLetters" should "accept strings of all letters" in {
    testPatternValidator(Validators.AllLetters,10 to 24,'A' to 'Z','a' to 'z')
  }

  "Validators.allDigits" should "accept strings of all digits" in {
    testPatternValidator(Validators.AllDigits,10 to 24,'0' to '9')
  }

  "Validators.allLettersOrDigits" should "accept strings of all letters or digits" in {
    testPatternValidator(Validators.AllLettersOrDigits,10 to 24,'A' to 'Z','a' to 'z','0' to '9')
  }

  "Validators.allLettersOrSpaces" should "accept strings of all letters or spaces" in {
    testPatternValidator(Validators.AllLettersOrSpaces,10 to 24,'A' to 'Z',' ' to ' ')
  }

  "Validators.allLettersDigitsOrSpaces" should "accept strings of all letters, digits or spaces" in {
    testPatternValidator(Validators.AllLettersDigitsOrSpaces,10 to 24,'A' to 'Z','0' to '9',' ' to ' ')
  }

  "Validators.NumberMinInclusive" should "reject numbers less than a certain value" in {
    val v = Validators.NumberMinInclusive(64)
    val failed = v.thisRules.toStream.map((Nil,_))
    // inclusive seam
    v(63) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "Validators.NumberMinInclusive" should "should accept numbers greater than or equal to a certain value" in {
    val v = Validators.NumberMinInclusive(64)
    // inclusive seam
    v(64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 64
      (v(n),n) should equal((noFail,n))
    }
  }

  "Validators.NmberMaxInclusive" should "reject numbers greater than a certain value" in {
    val v = Validators.NumberMaxInclusive(64)
    val failed = v.thisRules.toStream.map((Nil,_))
    // inclusive seam
    v(65) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63) + 65
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "Validators.NumberMaxInclusive" should "should accept numbers less than or equal to a certain value" in {
    val v = Validators.NumberMaxInclusive(64)
    // inclusive seam
    v(64) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(64)
      (v(n),n) should equal((noFail,n))
    }

  }

  "Validators.NumberMinExclusive" should "reject numbers below or equal to a certain value" in {
    val v = Validators.NumberMinExclusive(64)
    val failed = v.thisRules.toStream.map((Nil,_))
    // Exclusive seam
    v(64) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "Validators.NumberMinExclusive" should "should accept numbers greater than a certain value" in {
    val v = Validators.NumberMinExclusive(64)
    // Exclusive seam
    v(65) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(1024) + 65
      (v(n),n) should equal((noFail,n))
    }

  }

  "Validators.NumberMaxExclusive" should "reject numbers greater than or equal to a certain value" in {
    val v = Validators.NumberMaxExclusive(64)
    val failed = v.thisRules.toStream.map((Nil,_))
    // Exclusive seam
    v(64) should equal(failed)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63) + 64
      (v(n),n) should equal(
        (failed,n)
      )
    }
  }
  "Validators.NumberMaxExclusive" should "should accept less than a certain value" in {
    val v = Validators.NumberMaxExclusive(64)
    // Exclusive seam
    v(63) should equal(noFail)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(63)
      (v(n),n) should equal((noFail,n))
    }

  }
  
  "Validators.NumberRangeInclusive" should "should accept numbers between a certain range (inclusive)" in {
    val v = Validators.NumberRangeInclusive(20,64)
//    val failed = Metadata.Val(Rules.NumberRangeInclusive(20,64) :: Nil)
    val failMin = Stream((Nil,Rule.NumberMinInclusive(20)))
    val failMax = Stream((Nil,Rule.NumberMaxInclusive(64)))
    // min inclusive seam
    v(20) should equal(noFail)
    v(19) should equal(failMin)
    // max inclusive seam
    v(64) should equal(noFail)
    v(65) should equal(failMax)
    for(i <- 0 until TEST_COUNT) {
      val n = Random.nextInt(128)
      (n,v(n)) should equal(
        if(n < 20) (n,failMin) else if(n > 64) (n,failMax) else (n,noFail)
      )
    }

  }
}
