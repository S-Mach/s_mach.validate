package s_mach.validate

/**
 * Helper functions for validating collections
 */
object Collection {
  /**
   * A validator for a collection of A
   * @tparam A the type validated
   */
  def nonEmpty[A] =
    Validator.ensure[Traversable[A]](
      "must not be empty"
    )(_.nonEmpty)

  /**
   * A validator for a collection of A
   * @tparam A the type validated
   */
  def maxSize[A](max: Int) =
    Validator.ensure[Traversable[A]](
      s"must not have size greater than $max"
    )(_.size < max)

}
