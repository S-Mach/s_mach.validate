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

object DataTypeValidatorImplicits extends DataTypeValidatorImplicits
trait DataTypeValidatorImplicits {
  implicit val validator_Boolean = Validator.forVal[Boolean]()
  implicit val validator_Byte = Validator.forVal[Byte]()
  implicit val validator_Short = Validator.forVal[Short]()
  implicit val validator_Int = Validator.forVal[Int]()
  implicit val validator_Long = Validator.forVal[Long]()
  implicit val validator_Float = Validator.forVal[Float]()
  implicit val validator_Double = Validator.forVal[Double]()
  implicit val validator_Char = Validator.forVal[Char]()
  implicit val validator_String = Validator.forVal[String]()
  implicit val validator_BigInt = Validator.forVal[BigInt]()
  implicit val validator_BigDecimal = Validator.forVal[BigDecimal]()
}
