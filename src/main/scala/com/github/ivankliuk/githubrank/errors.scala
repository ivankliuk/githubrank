package com.github.ivankliuk.githubrank

import play.api.libs.json._

object errors {

  trait JsonError {
    def message: JsValue
  }

  case class RepositoryJsonError(message: JsValue) extends JsonError

  case class ServiceJsonError(message: JsValue) extends JsonError

  trait AppError[A] {
    def asServiceError(value: A): ServiceJsonError

    def asRepositoryError(value: A): RepositoryJsonError
  }

  object AppError {
    def jsonify(message: String): JsValue = Json.toJson(
      Json.obj(
        "message" -> JsString(message)
      )
    )
  }

}
