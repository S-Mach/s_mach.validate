package s_mach.validate

import java.util.NoSuchElementException

/** An ADT to indicate if validation failed */
sealed trait MaybeValid[+A] {
  /** @return the result of the validation */
  def result: ValidatorResult
  /** @return if invalid, failures */
  def failures: ValidatorResult
  /** @return the valid value or throws NoSuchElementException if validation failed */
  def get : A
  /** @return if valid, Some(value) otherwise None */
  def toOption : Option[A]
  
  def fold[X](
    ifInvalid: Invalid => X,
    ifValid: Valid[A] => X
    ) : X
}

object MaybeValid {
  def apply[A](a: A, result: ValidatorResult) : MaybeValid[A] =
    if(result.isEmpty) {
      Valid(a)
    } else {
      Invalid(result)
    }
}

case class Invalid (result: ValidatorResult) extends MaybeValid[Nothing] {
  def fold[X](
    ifInvalid: Invalid => X,
    ifValid: Valid[Nothing] => X
  ) : X = ifInvalid(this)

  def get = throw new NoSuchElementException
  def failures = result
  def toOption = None
}

case class Valid[+A] (
  value: A
) extends MaybeValid[A] {
  def result = Stream.empty
  def fold[X](
    ifInvalid: (Invalid) => X,
    ifValid: (Valid[A]) => X
  ) = ifValid(this)
  def get = value
  def toOption = Some(value)
  def failures = throw new NoSuchElementException
}

