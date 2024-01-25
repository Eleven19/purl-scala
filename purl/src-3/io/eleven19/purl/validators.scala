package io.eleven19.purl
import zio.prelude._
import scala.quoted.FromExpr
import components.Subpath
object validators {
  object SubpathValidator extends Validator[String](input =>
        Subpath.parse(input).map(_ => ()).toEitherWith { errors =>
          errors.reduceMapLeft(err => AssertionError.failure(err.getMessage)) { case (acc, err) =>
            acc ++ AssertionError.failure(err.getMessage)
          }
        }
      )
}
