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
package s_mach.validate.example

// Note: this needs to be here in package example b/c of imports
object ExampleUsage {

import scala.collection.immutable.StringOps
import play.api.libs.json._
import s_mach.codetools._
import s_mach.codetools.play_json._
import s_mach.explain_play_json.ExplainPlayJson
import s_mach.validate._
import s_mach.validate.play_json._
import s_mach.i18n._
import s_mach.validate.play_json._
import ExampleI18N._
  import s_mach.validate.Validators

/*
  Use Scala value-class (http://docs.scala-lang.org/overviews/core/value-classes.html)
  to restrict the value space of String. Name mostly can be treated as alias for
  String in code
*/
implicit class Name(
  val underlying: String
) extends AnyVal with IsValueClass[String]
object Name {
  import scala.language.implicitConversions
  /*
   Because Scala doesn't support recursive implicit resolution, need to
   add an implicit here to support using Name with StringOps methods such
   as foreach, map, etc
   */
  implicit def stringOps_Name(name: Name) = new StringOps(name.underlying)
  implicit val validator_Name =
    // Create a Validator[Name] based on a Validator[String]
    Validator.forValueClass[Name, String] {
      import Validators._
      // Build a Validator[String] by composing some pre-defined validators
      _ and StringNonEmpty and StringLengthMax(64) and AllLettersOrSpaces
    }
  implicit val format_Name =
    Json
      // Auto-generate a value-class format from the already existing implicit
      // Format[String]
      .forValueClass.format[Name,String]
      // Append the serialization-neutral Validator[Name] to the Play JSON Format[Name]
      .withValidator

  implicit val explainFormat_Name =
    ExplainPlayJson.forValueClass[Name,String].withValidator
}

/*
  Alternative to Scala value class: Distinct Type Alias
  Pros:
  1) No boxing/unboxing, alias is removed completely at runtime
  2) Collections/generics are never boxed and can be freely converted
  (e.g. List[Age] <=> List[Int])
  3) Tag mix-in allows distinctly resolving implicit type-classes for the type (implicit
  type-class resolution for plain type alias will always return type-class for underlying type)
  4) Not necessary to redeclare rich interface wrappers such as StringOps for Name value-class
  (see above)
  Cons:
  1) Scala doesn't automatically associate the same named companion object to the type alias,
  and so won't search for implicits inside the companion object. All implicits must be at
  top scope.
  2) Ugly
*/
trait AgeTag
type Age = Int with AgeTag with IsDistinctTypeAlias[Int]

import scala.language.implicitConversions
@inline implicit def Age(i: Int) = i.asInstanceOf[Age]

implicit val validator_Age = {
  Validator.forDistinctTypeAlias[Age,Int] {
    import Validators._
    _ and NumberRange(0, 150)
  }
}
implicit val format_Age =
  Json.forDistinctTypeAlias.format[Age,Int].withValidator

implicit val explainFormat_Age =
  ExplainPlayJson.forDistinctTypeAlias[Age,Int].withValidator

case class Person(id: Int, name: Name, age: Age)

object Person {
  implicit val validator_Person = {
    /*
      Macro generate a Validator for any product type (i.e. case class / tuple)
      that implicitly resolves all validators for declared fields. For Person,
      Validator[Int] for the id field, Validator[Name] for the name field and
      Validator[Age] for the age field are automatically composed into a
      Validator[Person].
     */
    Validator.forProductType[Person]
      // Compose the macro generated Validator[Person] with an additional condition
      .ensure(
        Rule('age_plus_id_must_be_less_than_$n,1000.toString)
        // p.age is used here as if it was an Int here without any extra code
      )(p => p.id + p.age < 1000)
  }
  implicit val format_Person = Json.format[Person].withValidator
  implicit val explainFormat_Person = ExplainPlayJson.forProductType[Person].withValidator
}

case class Family(
  father: Person,
  mother: Person,
  children: Seq[Person],
  grandMother: Option[Person],
  grandFather: Option[Person]
)

object Family {
  implicit val validator_Family =
  /*
     Macro generate a Validator for Family. Implicit methods in
     s_mach.validate.CollectionValidatorImplicits automatically handle creating
     Validators for Option and any Scala collection that inherits
     scala.collection.Traversable (as long as the contained type has an implicit
     Validator).
   */
    Validator.forProductType[Family]
      // Add some extra constaints
      .ensure(Rule(m_father_must_be_older_than_children.key)) { family =>
        family.children.forall(_.age < family.father.age)
      }
      .ensure(Rule(m_mother_must_be_older_than_children.key)) { family =>
        family.children.forall(_.age < family.mother.age)
      }

  implicit val format_Family = Json.format[Family].withValidator
  implicit val explainFormat_Family = ExplainPlayJson.forProductType[Family].withValidator
}

}