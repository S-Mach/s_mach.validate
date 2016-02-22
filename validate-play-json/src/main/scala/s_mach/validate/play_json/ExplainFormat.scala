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


import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox
import s_mach.codetools.IsValueClass
import s_mach.validate._
import s_mach.validate.impl.play_json._

/**
 * Type-class to fetch remarks to explain a Play JSON Format
 * */
trait ExplainFormat[A] {
  def jsonSchema: JsonSchema
}

object ExplainFormat {
  def apply[A](jsonSchemaInfo: JsonSchema) : ExplainFormat[A] =
    ExplainFormatOps.ExplainFormatImpl(jsonSchemaInfo)

  def builder[A] : ProductBuilder[ExplainFormat,A] =
    ExplainFormatForProduct[A](Map.empty)

  /** @return an ExplainFormat for a value */
  def forVal[A](jsi: JsonSchemaInfo.JsonVal) : ExplainFormat[A] =
    ExplainFormatOps.forVal(jsi)

  /** @return an ExplainFormat for a value class */
  def forValueClass[V <: IsValueClass[A],A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[V] = ExplainFormatOps.forValueClass(ea)

  /** @return an ExplainFormat for a distinct type alias */
  def forDistinctTypeAlias[V <: A,A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[V] = ExplainFormatOps.forDistinctTypeAlias(ea)

  /** @return an ExplainFormat for a product */
  def forProductType[A <: Product] : ExplainFormat[A] =
    macro macroForProductType[A]

  // Note: Scala requires this to be public
  def macroForProductType[A:c.WeakTypeTag](
    c: blackbox.Context
  ) : c.Expr[ExplainFormat[A]] = {
    val builder = new ExplainFormatMacroBuilder(c)
    builder.build[A]().asInstanceOf[c.Expr[ExplainFormat[A]]]
  }

  implicit def forOption[A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[Option[A]] = ExplainFormatOps.forOption

  implicit def forTraversable[M[AA] <: Traversable[AA],A](implicit
    ea: ExplainFormat[A]
  ) : ExplainFormat[M[A]] = ExplainFormatOps.forTraversable

}

