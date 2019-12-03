package com.github.ivankliuk.githubrank.models

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Github contributor.
 *
 * @param login Gihub username
 * @param contributions contributions amount
 */
case class Contributor(login: String, contributions: Int)

object Contributor {

  implicit val reads: Reads[Contributor] = (
    (JsPath \ "login").read[String] and
      (JsPath \ "contributions").read[Int]
    ) (Contributor.apply _)

  implicit def readsList: Reads[List[Contributor]] = Reads.list(reads)

  implicit val writes: Writes[Contributor] =
    (JsPath \ "login").write[String].and((JsPath \ "contributions").write[Int])(unlift(Contributor.unapply))

}

