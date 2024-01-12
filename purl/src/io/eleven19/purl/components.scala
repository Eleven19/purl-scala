package io.eleven19.purl

object components {
  type Namespace = Namespace.Type
  object Namespace extends Newtype[Option[String]] {
    val none: Namespace                             = None
    @inline def apply(namespace: String): Namespace = Some(namespace)
  }

  type Protocol = Protocol.Type
  object Protocol extends Newtype[String] {}

  type Name = Name.Type
  object Name extends Newtype[String] {}

  type Version = Version.Type
  object Version extends Newtype[Option[String]] {
    val none: Version                           = None
    @inline def apply(version: String): Version = Some(version)
  }

  type Subpath = Subpath.Type
  object Subpath extends Newtype[Option[String]] {
    val none: Subpath                           = None
    @inline def apply(subpath: String): Subpath = Some(subpath)
  }
}
