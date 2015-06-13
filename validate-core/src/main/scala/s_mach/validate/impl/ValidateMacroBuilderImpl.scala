package s_mach.validate.impl

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import s_mach.codetools.{Result, BlackboxHelper}
import s_mach.validate.Validator

class ValidateMacroBuilderImpl(
  val c: blackbox.Context,
  override val showDebug : Boolean = false
) extends BlackboxHelper {
  import c.universe._

  def build[A:c.WeakTypeTag]() : c.Expr[Validator[A]] =
    getOrAbort {
      calcProductType(c.weakTypeOf[A]).flatMap(build2)
    }

  case class FieldEx(
    field: ProductType.Field
  ) {
    import field._
    val name = field.name
    val termName = field.termName
    val validatorTermName = TermName("validator_" + termName)
    val _type = field._type
  }

  def build2[A:c.WeakTypeTag](
    aProductType: ProductType
  ) : Result[c.Expr[Validator[A]]] = {
    val aType = c.weakTypeOf[A]

    val fields = aProductType.oomField.map(FieldEx.apply)

    val result = c.Expr[Validator[A]] {
      q"""
new s_mach.validate.impl.ValidatorImpl[$aType] {
  ..${
    fields.map { field =>
      import field._
      q"val $validatorTermName = implicitly[Validator[${_type}]]"
    }
  }
  def apply(a: $aType) = {
    ..${aProductType.mkFieldValsTree(q"a")}
    ${
      fields.map { field =>
        import field._
        q"$validatorTermName($termName).map(_.pushPath($name))"
      }.reduce((a,b) => q"$a ::: $b")
    }
  }
  def rules = {
    ${
      fields.map { field =>
        import field._
        q"$validatorTermName.rules.map(_.pushPath($name))".asInstanceOf[c.Tree]
      }.reduce((a,b) => q"$a ::: $b")
    }
  }
  def schema = {
    Schema(Nil,${aType.toString},(1,1)) ::
    ${
      fields.map { field =>
        import field._
        q"$validatorTermName.schema.map(_.pushPath($name))".asInstanceOf[c.Tree]
      }.reduce((a,b) => q"$a ::: $b")
    }
  }
  def explain = {
    Schema(Nil,${aType.toString},(1,1)) ::
    ${
      fields.map { field =>
        import field._
        q"$validatorTermName.explain.map(_.pushPath($name))".asInstanceOf[c.Tree]
      }.reduce((a,b) => q"$a ::: $b")
    }
  }
}
       """
    }
    Result(result,Result.Debug(result.toString()))
  }
}

