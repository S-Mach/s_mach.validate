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

object ExampleUsage {

import scala.collection.immutable.StringOps
import s_mach.validate._
import play.api.libs.json._
import s_mach.validate.play_json._

// Use Scala value-class to restrict the value space of String
// Name can be treated as String in code
// See http://docs.scala-lang.org/overviews/core/value-classes.html
implicit class Name(
  val underlying: String
) extends AnyVal with IsValueClass[String]
object Name {
  import scala.language.implicitConversions
  // Because Scala doesn't support recursive implicit resolution, need to
  // add an implicit here to support using Name with StringOps methods such
  // as foreach, map, etc
  implicit def stringOps_Name(name: Name) = new StringOps(name.underlying)
  implicit val validator_Name =
    // Create a Validator[Name] based on a Validator[String]
    Validator.forValueClass[Name, String] {
      import Text._
      // Build a Validator[String] by composing some pre-defined validators
      nonEmpty and maxLength(64) and allLettersOrSpaces
    }

  implicit val format_Name =
    Json
      // Auto-generate a value-class format from the already existing implicit
      // Format[String]
      .forValueClass.format[Name,String](new Name(_))
      // Append the serialization-neutral Validator[Name] to the Play JSON Format[Name]
      .withValidator
}

implicit class Age(
  val underlying: Int
) extends AnyVal with IsValueClass[Int]
object Age {
  implicit val validator_Age = {
    import Validator._
    forValueClass[Age,Int](
      ensure(s"must be between (0,150)") { age =>
        0 <= age && age <= 150
      }
    )
  }
  implicit val format_Age =
    Json.forValueClass.format[Age,Int](new Age(_)).withValidator
}

case class Person(id: Int, name: Name, age: Age)

object Person {
  implicit val validator_Person = {
    import Validator._

    // Macro generate a Validator for any product type (i.e. case class / tuple)
    // that implicitly resolves all validators for declared fields. For Person,
    // Validator[Int] for the id field, Validator[Name] for the name field and
    // Validator[Age] for the age field are automatically composed into a
    // Validator[Person].
    forProductType[Person] and
    // Compose the macro generated Validator[Person] with an additional condition
    ensure(
      "age plus id must be less than 1000"
      // p.age is used here as if it was an Int here without any extra code
    )(p => p.id + p.age < 1000)
  }

  implicit val format_Person = Json.format[Person].withValidator
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
    // Macro generate a Validator for Family. Implicit methods in
    // s_mach.validate.CollectionValidatorImplicits automatically handle creating
    // Validators for Option and any Scala collection that inherits
    // scala.collection.Traversable (as long as the contained type has an implicit
    // Validator).
    // If set to None, Validator[Option[Person]], checks no Validator[Person] rules.
    // For Validator[M[A]] (where M[AA] <: Traversable[AA]) the rules of
    // Validator[Person] are checked for each Person in the collection.
    Validator.forProductType[Family]
      // Add some extra constaints using the optional builder syntax
      .ensure("father must be older than children") { family =>
        family.children.forall(_.age < family.father.age)
      }
      .ensure("mother must be older than children") { family =>
        family.children.forall(_.age < family.mother.age)
      }

  implicit val format_Family = Json.format[Family].withValidator
}

}