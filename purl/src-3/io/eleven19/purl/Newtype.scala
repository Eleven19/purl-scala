package io.eleven19.purl

abstract class Newtype[A] { self => 
    opaque type Type = A 
    def apply(a:A):Type = a
    extension (orig: Type) def value: A = orig

    def unapply(orig: Type): Some[A] = Some(orig.value)

    implicit val asEquivalence: Equivalence[A, Type] = new Newtype.Make[A, Type] {
        override def to: A => Type   = self.apply
        override def from: Type => A = self.value
    }
}

object Newtype {
    private[purl] trait Make[A, B] extends Equivalence[A, B]
}