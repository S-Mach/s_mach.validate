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

import scala.util.Random
import s_mach.codetools._
import s_mach.metadata._
import s_mach.i18n.messages._

object ValidatorTest {
  val m_test = 'test.literal

  implicit class Age(val underlying: Int) extends AnyVal with IsValueClass[Int]
  implicit class Name(val underlying: String) extends AnyVal with IsValueClass[String]

  case class Person(id: Int, name: Name, age: Age)

  object Implicits {

    implicit val validator_Int = Validator.forVal[Int]()
    // Note: not using Validator.forValueClass here for testing
    implicit val validator_Age = new Validator[Age] {
      val inner = Validators.NumberRangeInclusive(0,150)
      val thisRules = inner.thisRules
      val rules = TypeMetadata.Val(thisRules)
      def apply(a: Age) = Metadata.Val(inner(a.underlying).value)
      def and(other: Validator[Age]) = ???
    }
    // Note: not using Validator.forValueClass here for testing
    implicit val validator_Name = new Validator[Name] {
      val v1 = Validators.StringLengthRange(1,64)
      val v2 = Validators.AllLettersOrSpaces
      val thisRules = List(v1,v2).flatMap(_.thisRules)
      val rules = TypeMetadata.Val(thisRules)
      def apply(a: Name) = Metadata.Val(
        List(v1,v2).flatMap { r =>
          r(a.underlying).value
        }
      )
      def and(other: Validator[Name]) = ???
    }
  }
}
class ValidatorTest extends FlatSpec with Matchers {
  import ValidatorTest._

  "Empty Val Validator" should "create an empty validator" in {
    val v = Validator.forVal[Int]()
    v.rules should equal(TypeMetadata.Val(Nil))
  }

  "Empty Val Validator.and" should "always return the other validator" in {
    val v = Validator.forVal[Int]()
    val v2 = Validator.ensure[Int](Rule(m_test))(_ < 100)

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
        Seq(
          "id" -> TypeMetadata.Val(List.empty[Rule]),
          "name" -> TypeMetadata.Val(
            Rule.StringLengthMin(1) ::
            Rule.StringLengthMax(64) ::
            Rule.AllLettersOrSpaces ::
            Nil
          ),
          "age" -> TypeMetadata.Val(
            Rule.NumberMinInclusive(0) ::
            Rule.NumberMaxInclusive(150) ::
            Nil
          )
        )
      )
    )
    v(Person(1,"1" * 65,151)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Seq(
          "id" -> Metadata.Val(List.empty[Rule]),
          "name" -> Metadata.Val(
            Rule.StringLengthMax(64) ::
            Rule.AllLettersOrSpaces ::
            Nil
          ),
          "age" -> Metadata.Val(
            Rule.NumberMaxInclusive(150) ::
            Nil
          )
        )
      )
    )
    v(Person(1,"",1)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Seq(
          "id" -> Metadata.Val(List.empty[Rule]),
          "name" -> Metadata.Val(
            Rule.StringLengthMin(1) ::
            Nil
          ),
          "age" ->Metadata.Val(List.empty[Rule])
        )
      )
    )
    v(Person(1,"asdf",1)) should equal(Metadata.Rec(
      Nil,
      Seq(
        "id" -> Metadata.Val(Nil),
        "name" -> Metadata.Val(Nil),
        "age" -> Metadata.Val(Nil)
      )
    ))
  }

  "Validator.forProductType" should "create a product validator" in {
    import Implicits._

    val v = Validator.forProductType[Person]

    v.rules should equal(
      TypeMetadata.Rec[List[Rule]](
        Nil,
        Seq(
          "id" ->TypeMetadata.Val(List.empty[Rule]),
          "name" -> TypeMetadata.Val(
            Rule.StringLengthMin(1) ::
            Rule.StringLengthMax(64) ::
            Rule.AllLettersOrSpaces ::
            Nil
          ),
          "age" -> TypeMetadata.Val(
            Rule.NumberMinInclusive(0) ::
            Rule.NumberMaxInclusive(150) ::
            Nil
          )
        )
      )
    )
    v(Person(1,"1" * 65,151)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Seq(
          "id" -> Metadata.Val(List.empty[Rule]),
          "name" -> Metadata.Val(
            Rule.StringLengthMax(64) ::
            Rule.AllLettersOrSpaces ::
            Nil
          ),
          "age" -> Metadata.Val(
             Rule.NumberMaxInclusive(150) ::
             Nil
          )
        )
      )
    )
    v(Person(1,"",1)) should equal(
      Metadata.Rec[List[Rule]](
        Nil,
        Seq(
          "id" -> Metadata.Val(List.empty[Rule]),
          "name" -> Metadata.Val(
            Rule.StringLengthMin(1) ::
            Nil
          ),
          "age" ->Metadata.Val(List.empty[Rule])
        )
      )
    )
    v(Person(1,"asdf",1)) should equal(Metadata.Rec(
      Nil,
      Seq(
        "id" ->Metadata.Val(Nil),
        "name" ->Metadata.Val(Nil),
        "age" ->Metadata.Val(Nil)
      )
    ))
  }

  "Validator.forValueClass" should "create a validator for the underlying type" in {
    implicit val validator_String = Validator.forVal[String]()

    val v = Validator.forValueClass[Name,String](
      _ and Validators.StringLengthRange(1,64) and
        Validators.AllLettersOrSpaces
    )

    v.rules should equal(TypeMetadata.Val(
      Rule.StringLengthMin(1) ::
      Rule.StringLengthMax(64) ::
      Rule.AllLettersOrSpaces ::
      Nil
    ))

    v("1" * 65) should equal(Metadata.Val(
      Rule.StringLengthMax(64) ::
      Rule.AllLettersOrSpaces ::
      Nil
    ))
    v("") should equal(Metadata.Val(
      Rule.StringLengthMin(1) :: Nil
    ))
    v("asdf") should equal(Metadata.Val(Nil))
  }

  "Validator.ensure" should "create a validator with the check" in {
    val testRule = Rule(m_test)
    val v = Validator.ensure[Int](testRule)(_ < 100)

    v.rules should equal(TypeMetadata.Val(testRule :: Nil))
    v(101) should equal(Metadata.Val(testRule :: Nil))
    v(99) should equal(Metadata.Val(Nil))
  }

  "Validator.comment" should "create a validator with the comment as a rule" in {
    val testRule = Rule(m_test)
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
        Rule.StringLengthMin(1) ::
        Rule.StringLengthMax(64) ::
        Rule.AllLettersOrSpaces ::
        Nil
      )
    ))

    v(Some("1" * 65)) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      members = Seq(
        Metadata.Val(
          Rule.StringLengthMax(64) ::
          Rule.AllLettersOrSpaces ::
          Nil
        )
      )
    ))
    v(Some("")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      members = Seq(
        Metadata.Val(
          Rule.StringLengthMin(1) :: Nil
        )
      )
    ))
    v(Some("asdf")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      members = Seq(
        Metadata.Val(
          Nil
        )
    )
    ))
    v(None) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrOne,
      members = Seq.empty
    ))
  }

  "Validator.forTraversable" should "create a validator for any Traversable" in {
    import Implicits._

    val v = Validator.forTraversable[Seq,Name]

    v.rules should equal(TypeMetadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      TypeMetadata.Val(
        Rule.StringLengthMin(1) ::
        Rule.StringLengthMax(64) ::
        Rule.AllLettersOrSpaces ::
        Nil
      )
    ))

    v(Seq("1" * 65,"","asdf")) should equal(Metadata.Arr[List[Rule]](
      Nil,
      Cardinality.ZeroOrMore,
      members = Seq(
        Metadata.Val(
          Rule.StringLengthMax(64) ::
          Rule.AllLettersOrSpaces ::
          Nil
        ),
        Metadata.Val(
          Rule.StringLengthMin(1) ::
          Nil
        ),
        Metadata.Val[List[Rule]](
          Nil
        )
      )
    ))
    v(Seq("")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      members = Seq(
        Metadata.Val(
          Rule.StringLengthMin(1) :: Nil
        )
      )
    ))
    v(Seq("asdf")) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      members = Seq(Metadata.Val(
        Nil
      ))
    ))
    v(Seq.empty) should equal(Metadata.Arr(
      Nil,
      Cardinality.ZeroOrMore,
      members = Seq.empty
    ))
  }
}
