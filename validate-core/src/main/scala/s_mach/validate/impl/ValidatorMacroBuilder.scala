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
package s_mach.validate.impl

import scala.reflect.macros.blackbox
import s_mach.codetools.macros.{BlackboxHelper, Result}
import s_mach.validate._

class ValidatorMacroBuilder(
  val c: blackbox.Context,
  override val showDebug : Boolean = false
) extends BlackboxHelper {
  import c.universe._

  def build[A:c.WeakTypeTag]() : c.Expr[Validator[A]] =
    getOrAbort {
      calcProductType(c.weakTypeOf[A]).flatMap(build2)
    }

  case class FieldEx(
    field: ProductType.Field
  ) {
    val index = field.index
    val name = field.name
    val termName = field.termName
    val validatorTermName = TermName("validator_" + termName)
    val _type = field._type
  }

  def build2[A:c.WeakTypeTag](
    aProductType: ProductType
  ) : Result[c.Expr[Validator[A]]] = {
    val aType = c.weakTypeOf[A]

    val fields = aProductType.oomField.map(FieldEx.apply)

    val result = c.Expr[Validator[A]] {
      q"""
new s_mach.validate.impl.ValidatorImpl[$aType] {
  ..${
    fields.map { field =>
      import field._
      q"val $validatorTermName = implicitly[s_mach.validate.Validator[${_type}]]"
    }
  }
  val thisRules = Nil
  val rules = {
    s_mach.metadata.TypeMetadata.Rec(
      thisRules,
      Seq(..${
        fields.map { field =>
          import field._
          q"($name,$validatorTermName.rules)".asInstanceOf[c.Tree]
        }
      })
    )
  }

  override def validate(basePath: s_mach.metadata.Metadata.Path)(a: $aType) = {
    ${fields.map { field =>
      import field._
      q"$validatorTermName.validate(s_mach.metadata.Metadata.PathNode.SelectField($name) :: basePath)(a.$termName)".asInstanceOf[c.Tree]
    }.reduce((a,b) => q"$a ++ $b")}
  }
}
       """
    }
    Result(result,Result.Debug(result.toString()))
  }

  /*
  def apply(a: $aType) = {
    s_mach.metadata.Metadata.Rec(
      thisRules,
      Seq(..${
        fields.map { field =>
          import field._
          q"($name,$validatorTermName(a.$termName))".asInstanceOf[c.Tree]
        }
      })
    )
  }
   */
}
