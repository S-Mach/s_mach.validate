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
package s_mach.validate.codegen

object TupleValidatorImplicitsCodeGen {
  def gen(n: Int) = {
    val lcs = ('a' to 'z').map(_.toString).take(n)
    val ucs = ('A' to 'Z').map(_.toString).take(n)
    val allUcs = ucs.mkString(",")
    val allLcs = lcs.mkString(",")
    val allPatchUcs = ucs.map(uc => s"P$uc").mkString(",")
    val aType = s"($allUcs)"
    val pType = s"(${ucs.map(uc => s"Option[P$uc]").mkString(",")})"
    val typeParms = ucs.mkString(",")
s"""
  implicit def mkValidator_Tuple${n}[$typeParms](implicit
    ${(0 until n).map { i =>
      s"${lcs(i)}Validator: Validator[${ucs(i)}]"
    }.mkString(",\n    ")}
  ) : Validator[$aType] =
    Validator.forProductType[$aType]
"""
  }
  
  def genToFile(path: String) : Unit = {

    val contents =
s"""$header
package s_mach.validate
/* WARNING: Generated code. To modify see s_mach.validate.TupleValidatorImplicits */
trait TupleValidatorImplicits {
${(2 to 22).map(i => gen(i)).mkString("\n")}
}
"""
    
    import java.io._
    val out = new PrintWriter(new BufferedWriter(new FileWriter(path)))
    out.println(contents)
    out.close()
  }

}