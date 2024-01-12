package io.eleven19.purl
import components._
import scala.annotation.nowarn
import zio.test.{Result => _, _}

object PackageUrlSpec extends ZIOSpecDefault {
  @nowarn
  def spec = suite("PackageUrlSpec")(
    parsingSuite
  )

  def parsingSuite = suite("Parsing")(
    test("Can successfully parse a valid Bitbucket package url") {
      val input = "pkg:bitbucket/birkenfeld/pygments-main@244fd47e07d1014f0aed9c"
      val expected = Result.ok(PackageUrl(
        protocol = "bitbucket",
        namespace = Namespace("birkenfeld"),
        name = Name("pygments-main"),
        version = Version("244fd47e07d1014f0aed9c"),
        qualifiers = Map.empty,
        subpath = Subpath.none
      ))
      val actual = PackageUrl.parse(input)
      assertTrue(actual == expected)
    }
  )
}
