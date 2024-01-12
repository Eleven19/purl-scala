package io.eleven19.purl

sealed abstract class PurlError(val message: Option[String], underlyingCause: Throwable = null)
    extends Exception(message.orNull, underlyingCause) with Serializable {
  def cause: Option[Throwable] = Option(getCause())
}

object PurlError {
  sealed abstract class InvalidPurl(message: Option[String], underlyingCause: Throwable = null)
      extends PurlError(invalidPurlMessage(message), underlyingCause)

  final case class InvalidScheme(scheme: String) extends InvalidPurl(Some(s"Invalid scheme: $scheme"))
  final case class InvalidType(typ: String) extends InvalidPurl(Some(s"Invalid type/protocol: $typ")) {
    @inline def protocol: String = typ
  }
  final case class InvalidKey(key: String) extends InvalidPurl(Some(s"Invalid key: $key"))
  case object MissingName                  extends InvalidPurl(Some("Missing name"))
  final case class InvalidNamespaceComponent(namespace: String)
      extends InvalidPurl(Some(s"Invalid namespace component: $namespace"))
  case object MissingScheme extends InvalidPurl(Some("Missing scheme"))
  case object MissingType   extends InvalidPurl(Some("Missing type"))
  final case class InvalidSubpathSegment(segment: String)
      extends InvalidPurl(Some(s"Invalid subpath segment: $segment"))
  final case class MalformedPackageUrl(msg: String) extends InvalidPurl(Option(msg))

  private def invalidPurlMessage(detailMessage: Option[String]): Some[String] =
    detailMessage.fold(Some("Invalid Package URL"))(msg => Some(s"Invalid Package URL: $msg"))
}
