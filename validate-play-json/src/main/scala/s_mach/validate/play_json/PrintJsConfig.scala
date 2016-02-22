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


/**
 * Trait for configuration options for controlling how JSON is rendered
 * when using any of the printJs methods.
 */
trait PrintJsConfig {
  /**
   * Commonly in JSON serializers (and in Play's default macro generated
   * serializers) Options are not emitted as a distinct JSON node (such
   * as a JSON array). Instead, the underlying node is simply omitted if set
   * to None and emitted if set to Some(...). The collapseOption setting is
   * used to control whether Option should be emitted as a JSON array in the
   * output or collapsed into the underlying JSON node.
   *
   * @return TRUE to collapse Option
   *         (Metadata.Arr or TypeMetadata.Arr with Cardinality.ZeroOrOne) into
   *         the underlying field FALSE to render the Option as a JSON array
   *         with zero or one member.
   * */
  def collapseOption : Boolean
}

object PrintJsConfig {
  case class PrintJsConfigImpl(
    collapseOption: Boolean
  ) extends PrintJsConfig

  def apply(
    collapseOption: Boolean
  ) : PrintJsConfig = PrintJsConfigImpl(collapseOption)

  implicit val defaultPrintJsConfig = PrintJsConfig(
    collapseOption = true
  )
}
