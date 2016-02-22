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
package s_mach


import scala.language.implicitConversions
import scala.language.higherKinds
import s_mach.metadata._

package object validate extends
  TupleValidatorImplicits with
  DataTypeValidatorImplicits with
  CollectionValidatorImplicits with
  ValidatorImplicits
{
  /* Prefix added to implicits to prevent shadowing: FvWhDLaDRG */

  type TypeRemarks = TypeMetadata[List[String]]
  type Remarks = Metadata[List[String]]

  implicit class FvWhDLaDRG_PML[A](val self: A) extends AnyVal {
    /** @return list of rules that did not pass OR Nil if valid */
    def validate(implicit v:Validator[A]) : Metadata[List[Rule]] = v(self)
  }

  implicit class FvWhDLaDRG_RulePML(val self: Rule) extends AnyVal {
    def message(implicit mr: MessageForRule) : String =
      mr.messageFor(self)
  }

    implicit class FvWhDLaDRG_ValidatorPML[A](val self: Validator[A]) extends AnyVal {
    /** @return composite Validator of self and Validator.ensure */
    def ensure(
      rule: Rule
    )(
      f: A => Boolean
    ) : Validator[A] =
      self and Validator.ensure(rule)(f)

    /** @return composite Validator of self and Validator.comment */
    def comment(rule: Rule) : Validator[A] =
      self and Validator.comment[A](rule)

    /** @return an optional validator wrapper of self */
    def optional : Validator[Option[A]] = Validator.forOption(self)

    /** @return a collection validator wrapper of self */
    def zeroOrMore[M[AA] <: Traversable[AA]] : Validator[M[A]] =
      Validator.forTraversable(self)
  }

  implicit class FvWhDLaDRG_TypeMetadataPML(val self: TypeRemarks) {
    /** @return a list of string messages for the remarks in the for "path: remark" */
    def print : List[String] =
      self.nodes.toStream.flatMap { case (path,messages) =>
        messages.value.map(msg => s"${path.print}: $msg")
      }.toList
  }

  implicit class FvWhDLaDRG_MetadataPML(val self: Remarks) {
    /** @return a list of string messages for the remarks in the for "path: remark" */
    def print : List[String] =
      self.nodes.toStream.flatMap { case (path,messages) =>
        messages.value.map(msg => s"${path.print}: $msg")
      }.toList
  }

  implicit class FvWhDLaDRG_TypeMetadataListRule(val self: TypeMetadata[List[Rule]]) extends AnyVal {
    /** @return TypeRemarks for rule type metadata */
    def toTypeRemarks(implicit mr:MessageForRule) : TypeRemarks =
      self.map(_.map(_.message))
  }

  implicit class FvWhDLaDRG_MetadataListRule(val self: Metadata[List[Rule]]) extends AnyVal {
    /** @return Remarks for rule metadata */
    def toRemarks(implicit mr:MessageForRule) : Remarks =
      self.map(_.map(_.message))
  }
}
