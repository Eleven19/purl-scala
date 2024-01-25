package io.eleven19.purl
import components._
import scala.annotation.nowarn
import zio.test.{Result => _, _}

object SubpathSpec extends ZIOSpecDefault {
  @nowarn
  def spec = suite("SubpathSpec")(
    constructionSuite
  )

  def constructionSuite = suite("Construction")(
    test("Can construct a valid Subpath") {
      val sut     = Subpath.parse("path1/path2/path3")
      val subpath = sut.getOrElse(Subpath.none)
      assertTrue(subpath.text == "path1/path2/path3")
    }
  )
}
