package s_mach.validate

import java.util.NoSuchElementException

import s_mach.metadata.Metadata

/** An ADT to indicate if validation failed */
sealed trait MaybeValid[+A] {
  /** @return if invalid, failures */
  def failures: Metadata[List[Rule]]
  def fold[X](
    ifInvalid: Invalid => X,
    ifValid: Valid[A] => X
  ) : X
  /** @return the valid value or throws NoSuchElementException if validation failed */
  def get : A
  /** @return if valid, Some(value) otherwise None */
  def toOption : Option[A]
}

object MaybeValid {
  def apply[A](a: A, maybeFailures: Metadata[List[Rule]]) : MaybeValid[A] =
    if(maybeFailures.values.isEmpty) {
      Valid(a, maybeFailures)
    } else {
      Invalid(maybeFailures)
    }
}

case class Invalid (failures: Metadata[List[Rule]]) extends MaybeValid[Nothing] {
  def fold[X](
    ifInvalid: Invalid => X,
    ifValid: Valid[Nothing] => X
  ) : X = ifInvalid(this)

  def get = throw new NoSuchElementException
  def toOption = None
}

case class Valid[+A] (
  value: A,
  failures: Metadata[List[Rule]]
) extends MaybeValid[A] {
  def fold[X](
    ifInvalid: (Invalid) => X,
    ifValid: (Valid[A]) => X
  ) = ifValid(this)
  def get = value
  def toOption = Some(value)
}

