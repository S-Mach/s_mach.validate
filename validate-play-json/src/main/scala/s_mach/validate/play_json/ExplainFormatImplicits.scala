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

import scala.language.higherKinds
import JsonSchemaInfo._

object ExplainFormatImplicits extends ExplainFormatImplicits
trait ExplainFormatImplicits {

  def explainFormat[A](implicit ea: ExplainFormat[A]) = ea.jsonSchema

  implicit val explainFormat_Boolean = ExplainFormat.forVal[Boolean](JsonBoolean())
  implicit val explainFormat_Byte = ExplainFormat.forVal[Byte](JsonInteger())
  implicit val explainFormat_Short = ExplainFormat.forVal[Short](JsonInteger())
  implicit val explainFormat_Int = ExplainFormat.forVal[Int](JsonInteger())
  implicit val explainFormat_Long = ExplainFormat.forVal[Long](JsonInteger())
  implicit val explainFormat_Float = ExplainFormat.forVal[Float](JsonNumber())
  implicit val explainFormat_Double = ExplainFormat.forVal[Double](JsonNumber())
  implicit val explainFormat_Char = ExplainFormat.forVal[Char](JsonString())
  implicit val explainFormat_String = ExplainFormat.forVal[String](JsonString())
  implicit val explainFormat_BigInt = ExplainFormat.forVal[BigInt](JsonInteger())
  implicit val explainFormat_BigDecimal = ExplainFormat.forVal[BigDecimal](JsonNumber())

}
