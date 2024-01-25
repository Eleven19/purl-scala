package io.eleven19
import zio.prelude.Validation
import zio.prelude.ZValidation.Success

package object purl {
  type Result[+A] = Validation[PurlError, A]
  object Result {
    def err(error: PurlError): Result[Nothing] = Validation.fail(error)
    def ok[A](value: A): Result[A]             = Validation.succeed(value)
    val unit: Result[Unit]                     = ok(())
  }

  implicit final class ResultOps[+A](val self: Result[A]) extends AnyVal {
    @inline def isOk: Boolean = self match {
      case Success(_, _) => true
      case _             => false
    }
  }
}
