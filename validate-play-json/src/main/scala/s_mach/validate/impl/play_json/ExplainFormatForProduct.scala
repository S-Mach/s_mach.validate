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
package s_mach.validate.impl.play_json

import s_mach.metadata._
import s_mach.validate._
import s_mach.validate.play_json._

object ExplainFormatForProduct {
  case class FieldExplainFormat[A,B](
    unapply: A => B,
    efb: ExplainFormat[B]
  )
}

case class ExplainFormatForProduct[A](
  fields: Map[Field,ExplainFormatForProduct.FieldExplainFormat[A,_]]
) extends ExplainFormat[A] with ProductBuilder[ExplainFormat,A] {
  import ExplainFormatForProduct._


  val jsonSchema =
    TypeMetadata.Rec[JsonSchemaInfo](
      JsonSchemaInfo.JsonObject(),
      fields.map { case (f,fieldExplainFormat) =>
        (f,fieldExplainFormat.efb.jsonSchema)
      }
    )

  def field[B](
    name: String, 
    unapply: A => B
  )(
    f: ExplainFormat[B] => ExplainFormat[B]
  )(implicit baseVb: ExplainFormat[B]) = {
    val efb = f(baseVb)
    val index = if(fields.isEmpty) 0 else fields.maxBy(_._1.index)._1.index + 1
    val fef = FieldExplainFormat(unapply,efb)
    copy(fields =
      fields.+((Field(name, index), fef))
    )
  }

  /*
   Note: could override and here for efficiently combining product validators
   with other product validators. However, most product validators will be
   macro generated and can't be combined like this. So implementing this would
   be a wasted effort.
    */

  def build() = this
}