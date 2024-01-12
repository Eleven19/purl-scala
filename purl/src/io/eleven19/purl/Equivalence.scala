package io.eleven19.purl

trait Equivalence[A, B] extends Function[A, B] { self =>
  def to: A => B
  def from: B => A

  final def apply(a: A): B = to(a)

  /**
   * Composes this equivalence with the specified equivalence.
   */
  def >>>[C](that: Equivalence[B, C]): Equivalence[A, C] = self andThen that

  /**
   * A named method for `>>>`.
   */
  def andThen[C](that: Equivalence[B, C]): Equivalence[A, C] =
    Equivalence(self.to andThen that.to, self.from compose that.from)

  def compose[C](that: Equivalence[C, A]): Equivalence[C, B] = that andThen self

  /**
   * Flips this equivalence around.
   */
  def flip: Equivalence[B, A] = Equivalence(from, to)
}

object Equivalence {
  def apply[A, B](to0: A => B, from0: B => A): Equivalence[A, B] = new Equivalence[A, B] {
    override def to: A => B   = to0
    override def from: B => A = from0
  }

  def unapply[A, B](self: Equivalence[A, B]): Some[(A => B, B => A)] =
    Some((self.to, self.from))

  def identity[A]: Equivalence[A, A] = Equivalence(Predef.identity[A], Predef.identity[A])
}
