package com.github.ivankliuk.githubrank.utils

import cats.implicits._
import com.github.ivankliuk.githubrank.instances._
import com.github.ivankliuk.githubrank.syntax._
import com.github.ivankliuk.githubrank.{ErrorOr, ReadsList}
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.WSResponse

class WSResponseSerializer extends ResponseSerializer[WSResponse] {
  def to[A : ReadsList](responses: List[WSResponse]): ErrorOr[List[A]] = {
    val reads = implicitly[Reads[List[A]]]
    responses.flatTraverse { wsResponse =>
      Either.catchNonFatal {
        Json.parse(wsResponse.body).as[List[A]](reads)
      }
        .leftMap(_.getMessage.asServiceError)
    }
  }
}
