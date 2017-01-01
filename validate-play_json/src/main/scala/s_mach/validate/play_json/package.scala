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
       ;it1i ,,,:::;;;::1tti      s_mach.validate-play_json
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

import play.api.libs.json._
import s_mach.explain_play_json.ExplainPlayJson
import s_mach.i18n.I18NConfig
import s_mach.validate.play_json.impl.{ValidatePlayJsonOps, PlayJsonOps}

package object play_json {
  /* Suffix added to implicits to prevent shadowing: VJNoCcdSFL */

  implicit class ReadsPML_VJNoCcdSFL[A](val self:Reads[A]) extends AnyVal {
    /** @return a Reads[V] that wraps self with the implicit Validator rules so
      *         that after successfully parsing, all validator rules are also
      *         checked */
    def withValidator(implicit
      v: Validator[A],
      i18ncfg: I18NConfig
    ) : Reads[A] =
      Reads(PlayJsonOps.wrapReadsWithValidator(self.reads,v))
  }

  implicit class FormatPML_VJNoCcdSFL_[A](val self:Format[A]) extends AnyVal {
    /** @return a Format[V] that wraps self with the implicit Validator rules so
      *         that after successfully parsing, all validator rules are also
      *         checked */
    def withValidator(implicit
      v: Validator[A],
      i18ncfg: I18NConfig
    ) : Format[A] =
      Format(
        Reads(PlayJsonOps.wrapReadsWithValidator(self.reads,v)),
        self
      )
  }

  implicit class ExplainFormatPML_VJNoCcdSFL[A](val self: ExplainPlayJson[A]) extends AnyVal {
    /** @return a ExplainFormat that wraps self with an ExplainFormat
      *         for the implicit Validator rules */
    def withValidator(implicit
      va: Validator[A]
    ) : ExplainPlayJson[A] =
      ValidatePlayJsonOps.wrapExplainPlayJsonWithValidator(self,va)
  }

}
