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
         .t1i .,::;;; ;1tt        Copyright (c) 2014 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

object DataTypeValidatorImplicits extends DataTypeValidatorImplicits
trait DataTypeValidatorImplicits {
  implicit val validator_Byte = Validator.empty[Byte]
  implicit val validator_Short = Validator.empty[Short]
  implicit val validator_Int = Validator.empty[Int]
  implicit val validator_Long = Validator.empty[Long]
  implicit val validator_Float = Validator.empty[Float]
  implicit val validator_Double = Validator.empty[Double]
  implicit val validator_Char = Validator.empty[Char]
  implicit val validator_String = Validator.empty[String]
  implicit val validator_BigInt = Validator.empty[BigInt]
  implicit val validator_BigDecimal = Validator.empty[BigDecimal]
}
