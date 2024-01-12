package io.eleven19.purl

import io.eleven19.purl.Result

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
    val none: Subpath = None
    def parse(input: String): Result[Subpath] =
      if (input == null || input.trim().isEmpty()) {
        Result.ok(none)
      } else {
        validated(input.split("/"))
      }

    private def validated(segments: Array[String]): Result[Subpath] = {
      var err: Option[PurlError] = None
      val sb                     = new StringBuilder()
      if (segments != null) {
        var idx = 0
        while (idx < segments.length && err.isEmpty) {
          val segment = segments(idx)
          if ("..".equals(segment) || ".".equals(segment)) {
            err = Some(PurlError.MalformedPackageUrl(
              "Segments in the subpath may not be a period ('.') or repeated period ('..')"
            ))
          } else if (segment.contentEquals("/")) {
            err = Some(PurlError.MalformedPackageUrl(
              "Segments in the subpath may not contain a forward slash ('/')"
            ))
          } else if (segment.isEmpty()) {
            err = Some(PurlError.MalformedPackageUrl(
              "Segments in the subpath may not be empty"
            ))
          } else {
            if (sb.nonEmpty) {
              sb.append("/")
            }
            sb.append(segment)
          }
          idx += 1
        }
      }
      err.fold(Result.ok(Subpath(Option(sb.toString()))))(Result.err)

    }

  }
}
