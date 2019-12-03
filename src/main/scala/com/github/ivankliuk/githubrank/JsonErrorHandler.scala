package com.github.ivankliuk.githubrank

import javax.inject.Singleton
import com.github.ivankliuk.githubrank.errors.AppError
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class JsonErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful {
      Status(statusCode)(AppError.jsonify(s"Client error: $message"))
    }

  /*
   * In case of any unhandled exception is thrown we intercept and bring it to
   * the common error message format. We don't expose exception's details but
   * it should be collected by [[https://sentry.io]] or similar.
   */
  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] =
    Future.successful {
      InternalServerError(AppError.jsonify(s"Unhandled exception is thrown"))
    }

}
