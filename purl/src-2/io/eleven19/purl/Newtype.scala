package io.eleven19.purl

abstract class Newtype[A] { self =>
  type Base
  trait _Tag extends Any
  type Type <: Base with _Tag
  @inline final def apply(a: A): Type = a.asInstanceOf[Type]

  @inline final def value(x: Type): A =
    x.asInstanceOf[A]

  implicit final class Ops(val self: Type) {
    @inline final def value: A = Newtype.this.value(self)
  }

  implicit val asEquivalence: Equivalence[A, Type] = new Newtype.Make[A, Type] {
    override def to: A => Type   = self.apply
    override def from: Type => A = self.value
  }
}

object Newtype {
  private[purl] trait Make[A, B] extends Equivalence[A, B]
}
