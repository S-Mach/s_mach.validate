package s_mach.validate

object ExampleUsage {

import scala.collection.immutable.StringOps
import s_mach.validate._
import play.api.libs.json._
import s_mach.validate.play_json._

// Use Scala value-class to restrict the value space of String
// Name can be treated as String in code
// See http://www.scala-lang.org/api/current/index.html#scala.AnyVal
implicit class Name(
  val underlying: String
) extends AnyVal with IsValueType[String]
object Name {
  import scala.language.implicitConversions
  // Because Scala doesn't support recursive implicit resolution, need to
  // add an implicit here to support using Name with StringOps methods such
  // as foreach, map, etc
  implicit def stringOps_Name(name: Name) = new StringOps(name.underlying)
  implicit val valueType_Name = ValueType[Name, String](new Name(_))
  implicit val validator_Name =
    Validator.forValueType[Name, String] {
      import Text._
      // Build a Validator[String] by composing some pre-defined validators
      nonEmpty and maxLength(64) and allLettersOrSpaces
    }
}

implicit class Age(
  val underlying: Int
) extends AnyVal with IsValueType[Int]
object Age {
  implicit val valueType_Age = ValueType[Age,Int](new Age(_))
  implicit val validator_Age = {
    import Validator._
    forValueType[Age,Int](
      ensure(s"must be between (0,150)") { age =>
        0 <= age && age <= 150
      }
    )
  }
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

  // Append the serialization-neutral Validator[Person] to the Play JSON
  // Reads[Person] (in the Format[Person])
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
  implicit val validator_Family = {
    import Validator._

    // Macro generate a Validator for Family. Implicits methods in
    // s_mach.validate.CollectionValidatorImplicits automatically handle creating
    // Validators for Option and any Scala collection that inherits
    // scala.collection.Traversable (as long as the contained type has an implicit
    // Validator).
    // If set to None, Validator[Option[Person]], checks no Validator[Person] rules.
    // For Validator[M[A]] (where M[AA] <: Traversable[AA]) the rules of
    // Validator[Person] are checked for each Person in the collection.
    forProductType[Family] and
    ensure("father must be older than children") { family =>
      family.children.forall(_.age < family.father.age)
    } and
    ensure("mother must be older than children") { family =>
      family.children.forall(_.age < family.mother.age)
    }
  }

  implicit val format_Family = Json.format[Family].withValidator
}

}