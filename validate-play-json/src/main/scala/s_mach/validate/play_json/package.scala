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
package s_mach.validate

import s_mach.validate._
import play.api.libs.json._
import s_mach.codetools.IsValueClass
import s_mach.metadata._
import s_mach.validate.impl.play_json._

package object play_json
  extends
  ExplainFormatImplicits
{
  /* Prefix added to implicits to prevent shadowing: VJNoCcdSFL */

  type JsonSchema = TypeMetadata[JsonSchemaInfo]

  implicit class VJNoCcdSFL_MetadataPML[A](val self: Metadata[A]) extends AnyVal {
    /** @return json representation of metadata */
    def printJs(f: A => JsValue)(implicit cfg: PrintJsConfig) : JsValue =
      PlayJsonOps.printMetadataJs(self)(f)
  }

  implicit class VJNoCcdSFL_TypeMetadataPML[A](val self: TypeMetadata[A]) extends AnyVal {
    /** @return json representation of type metadata */
    def printJs(f: A => JsValue)(implicit cfg: PrintJsConfig) : JsValue =
      PlayJsonOps.printTypeMetadataJs(self)(f)
  }

  implicit class VJNoCcdSFL_TypeRemarksPML[A](val self: TypeRemarks) extends AnyVal {
    /** @return json representation of type remarks */
    def printJs(implicit cfg: PrintJsConfig) : JsValue =
      PlayJsonOps.printJsTypeRemarks(self)
  }

  implicit class VJNoCcdSFL_TypeMetadataListRulePML[A](val self: TypeMetadata[List[Rule]]) extends AnyVal {
    /** @return json representation of type remarks */
    def printJs(implicit
      mr:MessageForRule,
      cfg:PrintJsConfig
    ) : JsValue =
      PlayJsonOps.printJsTypeRemarks(self.toTypeRemarks)
  }

  implicit class VJNoCcdSFL_TypeMetadataJsonSchemaInfoPML[A](val self: JsonSchema) extends AnyVal {
    /** @return convert JsonSchemaInfo to list of remarks */
    def toTypeRemarks(implicit
      mr:MessageForRule
    ) : TypeRemarks =
      JsonSchemaOps.toTypeRemarks(self)
    /** @return json representation of type remarks */
    def printJs(implicit
      mr:MessageForRule,
      cfg:PrintJsConfig
    ) : JsValue =
      PlayJsonOps.printJsTypeRemarks(JsonSchemaOps.toTypeRemarks(self))
    /** @return json object representation of JsonSchema */
    def printJsonSchema(
      baseId: String
    )(implicit
      mr:MessageForRule
    ) : JsObject =
      JsonSchemaOps.printJsonSchema(baseId, self)
  }

  implicit class VJNoCcdSFL_MetadataListRulePML(val self: Metadata[List[Rule]]) extends AnyVal {
    /** @return self converted to Remarks and printed to JSON */
    def printJs(implicit
      mr:MessageForRule,
      cfg:PrintJsConfig
    ) : JsValue =
      PlayJsonOps.printJsRemarks(self.toRemarks)
  }

  object ValueClassJson {
    /** @return a Writes[V] that uses the implicit Writes[A] */
    def writes[V <: IsValueClass[A],A](implicit
      aWrites:Writes[A]
    ) : Writes[V] =
      Writes[V](v => aWrites.writes(v.underlying))

    /** @return a Reads[V] that uses the implicit Reads[A] */
    def reads[V <: IsValueClass[A],A](implicit
      f: A => V,
      aReads:Reads[A]
    ) : Reads[V] =
      Reads[V](js => aReads.reads(js).map(f))

    /** @return a Format[V] that uses the implicit Format[A] */
    def format[V <: IsValueClass[A],A](implicit
      f: A => V,
      aReads: Reads[A],
      aWrites: Writes[A]
    ) : Format[V] = Format(reads(f,aReads),writes)
  }

  object DistinctTypeAliasJson {
    /** @return a Writes[V] that uses the implicit Writes[A] */
    def writes[V <: A,A](implicit
      aWrites:Writes[A]
    ) : Writes[V] =
      Writes[V](v => aWrites.writes(v))

    /** @return a Reads[V] that uses the implicit Reads[A] */
    def reads[V <: A,A](implicit
      f: A => V,
      aReads:Reads[A]
    ) : Reads[V] =
      Reads[V](js => aReads.reads(js).map(f))

    /** @return a Format[V] that uses the implicit Format[A] */
    def format[V <: A,A](implicit
      f: A => V,
      aReads: Reads[A],
      aWrites: Writes[A]
    ) : Format[V] = Format(reads(f,aReads),writes)
  }

  implicit class VJNoCcdSFL_JsonTypePML(val self: Json.type) extends AnyVal {
    def forValueClass = ValueClassJson
    def forDistinctTypeAlias = DistinctTypeAliasJson
  }

  implicit class VJNoCcdSFL_ReadsPML[A](val self:Reads[A]) extends AnyVal {
    /** @return a Reads[V] that wraps self with the implicit Validator rules so
      *         that after successfully parsing, all validator rules are also
      *         checked */
    def withValidator(implicit
      v: Validator[A],
      mr: MessageForRule
    ) : Reads[A] =
      Reads(PlayJsonOps.wrapReadsWithValidator(self.reads,v))
  }

  implicit class VJNoCcdSFL_FormatPML[A](val self:Format[A]) extends AnyVal {
    /** @return a Format[V] that wraps self with the implicit Validator rules so
      *         that after successfully parsing, all validator rules are also
      *         checked */
    def withValidator(implicit
      v: Validator[A],
      mr: MessageForRule
    ) : Format[A] =
      Format(
        Reads(PlayJsonOps.wrapReadsWithValidator(self.reads,v)),
        self
      )
  }

  implicit class VJNoCcdSFL_ExplainFormatPML[A](val self: ExplainFormat[A]) extends AnyVal {
    /** @return a ExplainFormat that wraps self with an ExplainFormat
      *         for the implicit Validator rules */
    def withValidator(implicit
      va: Validator[A],
      mr: MessageForRule
    ) : ExplainFormat[A] =
      ExplainFormatOps.wrapExplainFormatWithValidator(self,va)
  }

  implicit class VJNoCcdSFL_JsValuePML(val self: JsValue) extends AnyVal {
    /** @return string with pretty-printed output of JsValue */
    def pretty : String = Json.prettyPrint(self)
  }

}
