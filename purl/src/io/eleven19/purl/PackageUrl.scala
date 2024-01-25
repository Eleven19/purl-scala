package io.eleven19.purl
import zio.prelude._
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
      subpath <- Subpath.parse(uri.getRawFragment())
      remainder = new StringBuilder(uri.getRawSchemeSpecificPart())
    } yield PackageUrl(
      protocol = Protocol(""),
      namespace = Namespace.none,
      name = Name(uri.getRawPath()),
      version = Version.none,
      qualifiers = Map.empty,
      subpath = subpath
    )

  object Validations {
    def validateInputNotEmpty(input: String): Result[String] =
      if (input == null || input.trim().isEmpty()) {
        Validation.fail(PurlError.MissingScheme)
      } else {
        Validation.succeed(input)
      }

    def validatedUri(input: String): Result[URI] =
      try
        Validation.succeed(new URI(input))
      catch {
        case e: URISyntaxException => Validation.fail(PurlError.MalformedPackageUrl(e.getMessage()))
      }

    def validateNoUserInfo(uri: URI): Result[URI] =
      if (uri.getUserInfo() != null) {
        Validation.fail(PurlError.MalformedPackageUrl("User info is not allowed"))
      } else {
        Validation.succeed(uri)
      }

    def validatePort(uri: URI): Result[URI] = {
      val port = uri.getPort()
      if (port != -1) {
        Validation.fail(PurlError.MalformedPackageUrl(s"Port is not allowed but is present, port: port"))
      } else {
        Validation.succeed(uri)
      }
    }

    def validatedScheme(uri: URI): Result[String] = {
      val scheme = uri.getScheme()
      if (scheme.equals("pkg")) {
        Validation.succeed(scheme)
      } else {
        Validation.fail(PurlError.InvalidScheme(scheme))
      }
    }
  }
}
 