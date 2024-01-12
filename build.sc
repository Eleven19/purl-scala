import $ivy.`io.eleven19.mill::mill-crossbuild::0.1.0`
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.5`
import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import $ivy.`io.chris-kipp::mill-ci-release::0.1.9`
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:$MILL_VERSION`
import $ivy.`com.carlosedp::mill-aliases::0.4.1`

import com.goyeau.mill.scalafix.ScalafixModule
import com.carlosedp.aliases._
import coursier.maven.MavenRepository
import io.kipp.mill.ci.release.CiReleaseModule
import io.github.davidgregory084.TpolecatModule
import io.eleven19.mill.crossbuild._
import mill._, mill.scalalib._, mill.scalajslib._, mill.scalanativelib._, scalafmt._
import mill.contrib.buildinfo.BuildInfo

object purl extends Cross[PurlModule](V.scalaVersions)
trait PurlModule extends Cross.Module[String] with CrossPlatform {
  trait CommonScalaModule      extends ScalaModule with TpolecatModule with ScalafixModule with ScalafmtModule
  trait CommonCrossScalaModule extends CrossScalaModule with CommonScalaModule with CrossValue

  trait CommonCrossPlatformScalaModule extends CrossPlatformScalaModule with CommonCrossScalaModule

  trait CommonJvmModule extends CommonCrossPlatformScalaModule {
    def platform = Platform.JVM
  }
  trait CommonJsModule extends ScalaJSModule with CommonCrossPlatformScalaModule {
    def platform       = Platform.JS
    def scalaJSVersion = "1.15.0"
  }
  trait CommonNativeModule extends ScalaNativeModule with CommonCrossPlatformScalaModule {
    def platform           = Platform.Native
    def scalaNativeVersion = "0.4.16"
  }

  trait CommonTestModule extends TestModule.ZioTest {
    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"dev.zio::zio-test::${V.zio}",
      ivy"dev.zio::zio-test-sbt::${V.zio}"
    )

  }

  trait Shared extends CommonCrossPlatformScalaModule {}

  object jvm extends Shared with CommonJvmModule {
    object test extends ScalaTests with CommonTestModule
  }

  object js extends Shared with CommonJsModule {
    object test extends ScalaJSTests with CommonTestModule {
      override def ivyDeps = super.ivyDeps() ++ Agg(
        ivy"io.github.cquiroz::scala-java-time::${V.`scala-java-time`}",
        ivy"io.github.cquiroz::scala-java-time-tzdb::${V.`scala-java-time`}"
      )
    }
  }

  object native extends Shared with CommonNativeModule {
    object test extends ScalaNativeTests with CommonTestModule
  }
}

//------------------------------------------------------------------------------------
// Common
//------------------------------------------------------------------------------------

/// Versions
object V {
  val scalaVersions = Seq("2.13.12", "3.3.1")

  val `scala-java-time` = "2.5.0"
  val zio               = "2.0.21"
}
