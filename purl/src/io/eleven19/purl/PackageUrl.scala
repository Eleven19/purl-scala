package io.eleven19.purl
import components._
import java.net.{URI, URISyntaxException}

final case class PackageUrl(
    protocol: Protocol,
    namespace: Namespace,
    name: Name,
    version: Version,
    qualifiers: Map[String, String],
    subpath: Subpath
) {
  @inline val typ: Protocol = protocol
}

object PackageUrl {

  def parse(input: String): Result[PackageUrl] =
    for {
      _       <- Validations.validateInputNotEmpty(input)
      uri     <- Validations.validatedUri(input)
      _       <- Validations.validateNoUserInfo(uri)
      _       <- Validations.validatePort(uri)
      _       <- Validations.validatedScheme(uri)
      subpath <- Validations.validatedSubpath(uri)
      remainder = new StringBuilder(uri.getRawSchemeSpecificPart())
    } yield PackageUrl(
      protocol = "",
      namespace = Namespace.none,
      name = uri.getRawPath(),
      version = Version.none,
      qualifiers = Map.empty,
      subpath = subpath
    )

  object Validations {
    def validateInputNotEmpty(input: String): Result[Any] =
      if (input == null || input.trim().isEmpty()) {
        Result.err(PurlError.MissingScheme)
      } else {
        Result.unit
      }

    def validatedUri(input: String): Result[URI] =
      try
        Result.ok(new URI(input))
      catch {
        case e: URISyntaxException => Result.err(PurlError.MalformedPackageUrl(e.getMessage()))
      }

    def validateNoUserInfo(uri: URI): Result[Any] =
      if (uri.getUserInfo() != null) {
        Result.err(PurlError.MalformedPackageUrl("User info is not allowed"))
      } else {
        Result.unit
      }

    def validatePort(uri: URI): Result[Any] = {
      val port = uri.getPort()
      if (port != -1) {
        Result.err(PurlError.MalformedPackageUrl(s"Port is not allowed but is present, port: port"))
      } else {
        Result.unit
      }
    }

    def validatedScheme(uri: URI): Result[String] = {
      val scheme = uri.getScheme()
      if (scheme.equals("pkg")) {
        Result.ok(scheme)
      } else {
        Result.err(PurlError.InvalidScheme(scheme))
      }
    }

    def validatedSubpath(uri: URI): Result[Subpath] = {
      val fragment = uri.getRawFragment()
      if (fragment != null && !fragment.isEmpty()) {
        validatedSubpath(fragment)
      } else {
        Result.ok(None)
      }
    }

    def validatedSubpath(value: String): Result[Subpath] =
      if (value != null && !value.isEmpty()) {
        Result.ok(Some(value))
      } else {
        Result.ok(None)
      }
  }
}
