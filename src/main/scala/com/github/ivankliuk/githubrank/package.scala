package com.github.ivankliuk

import monix.eval.Task
import com.github.ivankliuk.githubrank.errors.{AppError, JsonError, RepositoryJsonError, ServiceJsonError}
import play.api.libs.json.{Json, Reads}

import scala.util.Try

package object githubrank {

  type Error = JsonError

  type ErrorOr[A] = Either[Error, A]

  type Effect[A] = Task[ErrorOr[A]]

  type ReadsList[A] = Reads[List[A]]

  type URL = String

  object instances {

    implicit val error: AppError[String] =
      new AppError[String] {
        def asServiceError(value: String): ServiceJsonError =
          ServiceJsonError {
            AppError.jsonify(s"Service error: $value")
          }

        /**
         * GitHub API returns error in JSON format. The method serializes it to
         * Play JSON object, traverses and extracts error message.
         * If serialization fails we fall back with passed value.
         */
        def asRepositoryError(value: String): RepositoryJsonError = {
          val extractedValue = Try {
            (Json.parse(value) \\ "message")
              .headOption
              .map(_.as[String])
          }
            .toOption
            .flatten
            .getOrElse(value)

          RepositoryJsonError(AppError.jsonify(s"Github error: $extractedValue"))
        }
      }

  }

  object syntax {

    implicit class ErrorOps[A](value: A) {
      def asServiceError(implicit e: AppError[A]): ServiceJsonError = e.asServiceError(value)

      def asRepositoryError(implicit e: AppError[A]): RepositoryJsonError = e.asRepositoryError(value)
    }

  }

}
