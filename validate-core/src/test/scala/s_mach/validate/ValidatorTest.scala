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
package s_mach.validate

import org.scalatest.{FlatSpec, Matchers}
import s_mach.validate.impl._
import Validators._
import scala.util.Random
import s_mach.codetools._
import s_mach.metadata._

object ValidatorTest {
  implicit class Age(val underlying: Int) extends AnyVal with IsValueClass[Int]
  implicit class Name(val underlying: String) extends AnyVal with IsValueClass[String]

  case class Person(id: Int, name: Name, age: Age)

  object Implicits {

    implicit val validator_Int = Validator.empty[Int]
    // Note: not using Validator.forValueClass here for testing
    implicit val validator_Age = new Validator[Age] {
      val inner = numberRange(0,150)
      val thisRules = inner.thisRules
      val rules = TypeMetadata.Val(thisRules)
      def apply(a: Age) = Metadata.Val(inner(a.underlying).value)
      def and(other: Validator[Age]) = ???
    }
    // Note: not using Validator.forValueClass here for testing
    implicit val validator_Name = new Validator[Name] {
      val r1 = stringNonEmpty
      val r2 = stringLengthMax(64)
      val r3 = allLettersOrSpaces
      val thisRules = List(r1,r2,r3).flatMap(_.thisRules)
      val rules = TypeMetadata.Val(thisRules)
      def apply(a: Name) = Metadata.Val(
        List(r1,r2,r3).flatMap { r =>
          r(a.underlying).value
        }
      )
      def and(other: Validator[Name]) = ???
    }
  }
}
class ValidatorTest extends FlatSpec with Matchers {
  import ValidatorTest._

  "Validator.empty" should "create an empty validator" in {
    val v = Validator.empty[Int]
    v.rules should equal(TypeMetadata.Val(Nil))
  }

  "Validator.empty.and" should "always return the other validator" in {
    val v = Validator.empty[Int]
    val v2 = Validator.ensure[Int](Rule("test"))(_ < 100)

    (v and v2) eq v2 should equal(true)
  }

  "Validator.builder" should "create a builder for creating a product validator" in {
    import Implicits._

    val v =
      Validator.builder[Person]
        .field("id",_.id)()
        .field("name",_.name)()
        .field("age",_.age)()
        .build()

    v.rules should equal(
      TypeMetadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),TypeMetadata.Val(List.empty[Rule])),
          (MetaField("name",1),TypeMetadata.Val(
            stringLengthMin(1).thisRules :::
            stringLengthMax(64).thisRules :::
            allLettersOrSpaces.thisRules
          )),
          (MetaField("age",2),TypeMetadata.Val(
            numberRange(0,150).thisRules
          ))
        )
      )
    )
    v(Person(1,"1" * 65,151)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),Metadata.Val(List.empty[Rule])),
          (MetaField("name",1),Metadata.Val(
            stringLengthMax(64).thisRules :::
            allLettersOrSpaces.thisRules
          )),
          (MetaField("age",2),Metadata.Val(
            numberMaxInclusive(150).thisRules
          ))
        )
      )
    )
    v(Person(1,"",1)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),Metadata.Val(List.empty[Rule])),
          (MetaField("name",1),Metadata.Val(
            stringNonEmpty.thisRules
          )),
          (MetaField("age",2),Metadata.Val(List.empty[Rule]))
        )
      )
    )
    v(Person(1,"asdf",1)) should equal(Metadata.Rec(
      Nil,
      Map(
        (MetaField("id",0),Metadata.Val(Nil)),
        (MetaField("name",1),Metadata.Val(Nil)),
        (MetaField("age",2),Metadata.Val(Nil))
      )
    ))
  }

  "Validator.forProductType" should "create a product validator" in {
    import Implicits._

    val v = Validator.forProductType[Person]

    v.rules should equal(
      TypeMetadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),TypeMetadata.Val(List.empty[Rule])),
          (MetaField("name",1),TypeMetadata.Val(
            stringLengthMin(1).thisRules :::
            stringLengthMax(64).thisRules :::
            allLettersOrSpaces.thisRules
          )),
          (MetaField("age",2),TypeMetadata.Val(
            numberRange(0,150).thisRules
          ))
        )
      )
    )
    v(Person(1,"1" * 65,151)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),Metadata.Val(List.empty[Rule])),
          (MetaField("name",1),Metadata.Val(
            stringLengthMax(64).thisRules :::
            allLettersOrSpaces.thisRules
          )),
          (MetaField("age",2),Metadata.Val(
            numberMaxInclusive(150).thisRules
          ))
        )
      )
    )
    v(Person(1,"",1)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Map(
          (MetaField("id",0),Metadata.Val(List.empty[Rule])),
          (MetaField("name",1),Metadata.Val(
            stringNonEmpty.thisRules
          )),
          (MetaField("age",2),Metadata.Val(List.empty[Rule]))
        )
      )
    )
    v(Person(1,"asdf",1)) should equal(Metadata.Rec(
      Nil,
      Map(
        (MetaField("id",0),Metadata.Val(Nil)),
        (MetaField("name",1),Metadata.Val(Nil)),
        (MetaField("age",2),Metadata.Val(Nil))
      )
    ))
  }

  "Validator.forValueClass" should "create a validator for the underlying type" in {
    implicit val validator_String = Validator.empty[String]

    val v = Validator.forValueClass[Name,String](
      _ and stringNonEmpty and
        stringLengthMax(64) and
        allLettersOrSpaces
    )

    v.rules should equal(TypeMetadata.Val(
      stringNonEmpty.thisRules :::
      stringLengthMax(64).thisRules :::
      allLettersOrSpaces.thisRules :::
      Nil
    ))

    v("1" * 65) should equal(Metadata.Val(
      stringLengthMax(64).thisRules :::
      allLettersOrSpaces.thisRules
    ))
    v("") should equal(Metadata.Val(
      stringNonEmpty.thisRules
    ))
    v("asdf") should equal(Metadata.Val(Nil))
  }

  "Validator.ensure" should "create a validator with the check" in {
    val testRule = Rule("test")
    val v = Validator.ensure[Int](testRule)(_ < 100)

    v.rules should equal(TypeMetadata.Val(testRule :: Nil))
    v(101) should equal(Metadata.Val(testRule :: Nil))
    v(99) should equal(Metadata.Val(Nil))
  }

  "Validator.comment" should "create a validator with the comment as a rule" in {
    val testRule = Rule("test")
    val v = Validator.comment[String](testRule)

    v.rules should equal(TypeMetadata.Val(testRule :: Nil))
    v(Random.nextString(20)) should equal(Metadata.Val(Nil))
  }

  "Validator.forOption" should "create a validator for Option" in {
    import Implicits._

    val v = Validator.forOption[Name]

    v.rules should equal(TypeMetadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      TypeMetadata.Val(
        stringNonEmpty.thisRules :::
        stringLengthMax(64).thisRules :::
        allLettersOrSpaces.thisRules
      )
    ))

    v(Some("1" * 65)) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      indexToMember = Map((0,Metadata.Val(
        stringLengthMax(64).thisRules :::
        allLettersOrSpaces.thisRules
      )))
    ))
    v(Some("")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      indexToMember = Map((0,Metadata.Val(
        stringNonEmpty.thisRules
      )))
    ))
    v(Some("asdf")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      indexToMember = Map((0,Metadata.Val(
        Nil
      )))
    ))
    v(None) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      indexToMember = Map.empty
    ))
  }

  "Validator.forTraversable" should "create a validator for any Traversable" in {
    import Implicits._

    val v = Validator.forTraversable[Seq,Name]

    v.rules should equal(TypeMetadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      TypeMetadata.Val(
        stringNonEmpty.thisRules :::
        stringLengthMax(64).thisRules :::
        allLettersOrSpaces.thisRules
      )
    ))

    v(Seq("1" * 65,"","asdf")) should equal(Metadata.Arr[List[Rule]](
      Nil,
      Cardinality.ZeroOrMore,
      indexToMember = Map(
        (0,Metadata.Val(
          stringLengthMax(64).thisRules :::
          allLettersOrSpaces.thisRules
        )),
        (1,Metadata.Val(
          stringNonEmpty.thisRules
        )),
        (2,Metadata.Val[List[Rule]](
          Nil
        ))
      )
    ))
    v(Seq("")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      indexToMember = Map(
        (0,Metadata.Val(
          stringNonEmpty.thisRules
        ))
      )
    ))
    v(Seq("asdf")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      indexToMember = Map((0,Metadata.Val(
        Nil
      )))
    ))
    v(Seq.empty) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      indexToMember = Map.empty
    ))
  }
}
