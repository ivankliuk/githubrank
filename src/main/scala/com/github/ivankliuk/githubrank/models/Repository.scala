package com.github.ivankliuk.githubrank.models

import play.api.libs.json._

/**
 * Github repository.
 *
 * @param fullName repository name in `username/repo_name` format.
 */
case class Repository(fullName: String) extends AnyVal

object Repository {
  implicit val reads: Reads[Repository] =
    (JsPath \ "full_name")
      .read[String].map(Repository.apply)

  implicit def readsList: Reads[List[Repository]] = Reads.list(reads)

}

