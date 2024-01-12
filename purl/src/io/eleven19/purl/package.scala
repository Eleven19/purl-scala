package io.eleven19

package object purl {
  type Result[+A] = Either[PurlError, A]
  object Result {
    def err(error: PurlError): Result[Nothing] = Left(error)
    def ok[A](value: A): Result[A]             = Right(value)
    val unit: Result[Unit]                     = ok(())
  }

  implicit final class ResultOps[+A](val self: Result[A]) extends AnyVal {
    @inline def isOk: Boolean = self.isRight
  }
}
