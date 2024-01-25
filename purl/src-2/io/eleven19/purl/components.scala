package io.eleven19.purl
import zio.prelude._

object components {
  type Namespace = Namespace.Type
  object Namespace extends Subtype[Option[String]] {
    val none: Namespace                     = wrap(None)
    def apply(namespace: String): Namespace = wrap(Some(namespace))
  }

  type Protocol = Protocol.Type
  object Protocol extends Newtype[String] {}

  type Name = Name.Type
  object Name extends Newtype[String] {}

  type Version = Version.Type
  object Version extends Newtype[Option[String]] {
    val none: Version                           = wrap(None)
    @inline def apply(version: String): Version = wrap(Option(version))
  }

  type Subpath = Subpath.Type
  object Subpath extends Newtype[Option[String]] {
    val none: Subpath = wrap(None)

    override def assertion = assertCustom { value =>
      parse(value.orNull).map(_ => ()).toEitherWith { errors =>
        errors.reduceMapLeft(err => AssertionError.failure(err.getMessage)) { case (acc, err) =>
          acc ++ AssertionError.failure(err.getMessage)
        }
      }
    }

    def parse(input: String): Result[Subpath] =
      if (input == null || input.trim().isEmpty()) {
        Result.ok(none)
      } else {
        validated(input.split("/"))
      }

    private def validated(segments: Array[String]): Result[Subpath] = {
      def validateNonEmpty(segment: String): Result[String] =
        if (segment.isEmpty()) {
          Result.err(PurlError.MalformedPackageUrl(
            "Segments in the subpath may not be empty"
          ))
        } else {
          Result.ok(segment)
        }

      def validateNoForwardSlash(segment: String): Result[String] =
        if (segment.contentEquals("/")) {
          Result.err(PurlError.MalformedPackageUrl(
            "Segments in the subpath may not contain a forward slash ('/')"
          ))
        } else {
          Result.ok(segment)
        }

      def validateNoPeriod(segment: String): Result[String] =
        if ("..".equals(segment) || ".".equals(segment)) {
          Result.err(PurlError.MalformedPackageUrl(
            "Segments in the subpath may not be a period ('.') or repeated period ('..')"
          ))
        } else {
          Result.ok(segment)
        }

      def validateSegement(segment: String): Result[String] =
        validateNonEmpty(segment) <& validateNoForwardSlash(segment) <& validateNoPeriod(segment)

      if (segments == null || segments.isEmpty) {
        Result.ok(none)
      } else {
        segments.foldLeft(Result.ok(new StringBuilder())) { (acc, segment) =>
          acc.zipWithPar(validateSegement(segment)) { (sb, segment) =>
            if (sb.nonEmpty) {
              sb.append("/")
            }
            sb.append(segment)
          }
        }.map { sb =>
          Subpath.wrap(Option(sb.toString()))
        }
      }
    }

    implicit class Ops(private val subpath: Subpath) extends AnyVal {
      def isEmpty: Boolean = unwrap(subpath) match {
        case None        => true
        case Some(value) => value.isEmpty()
      }
      def nonEmpty: Boolean = !isEmpty
      def text: String      = unwrap(subpath).getOrElse("")
    }

  }
}
