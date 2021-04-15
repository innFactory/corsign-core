package corsign.core.validation.utils

import scala.util.Either

object RightMap {
  implicit class EitherCompat[A, B](either: Either[A, B]) {
    //Scala 2.13 deprecates either.right.map, but Scala 2.11 isn't right-biased yet. This removes the deprecation warning.
    def rightMap[B1](f: B => B1): Either[A, B1] =
      either match {
        case Right(value) => Right(f(value))
        case Left(value)  => Left(value)
      }
  }
}
