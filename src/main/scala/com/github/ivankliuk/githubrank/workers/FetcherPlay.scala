package com.github.ivankliuk.githubrank.workers

import com.github.ivankliuk.githubrank.Effect
import com.github.ivankliuk.githubrank.instances._
import com.github.ivankliuk.githubrank.syntax._
import com.github.ivankliuk.githubrank.utils.Transformations
import javax.inject.Inject
import monix.eval.Task
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 * Concrete implementation of [[Fetcher]] to operate with [[monix.eval.Task]]
 * and [[WSResponse]].
 *
 * @param ws [[WSClient]] instance
 */
class FetcherPlay @Inject()(ws: WSClient) extends Fetcher[Task, WSResponse] {

  import FetcherPlay._

  def extractPaginatedUrls(response: WSResponse): Either[String, List[String]] =
    Transformations.extractPaginatedUrls(response)

  def request(url: String): Effect[WSResponse] =
    Task.fromFuture {
      ws.url(url).withHttpHeaders(Headers: _*).get
        .map { response =>
          response.status match {
            case Status.OK => Right(response)
            case _ => Left(response.body.asRepositoryError)
          }
        }
    }

}

object FetcherPlay {

  val OAuthToken = Try(sys.env("GH_TOKEN")).toOption

  val Headers = List("User-Agent" -> "GitHubRank") ++
    OAuthToken.map(t => "Authorization" -> s"token $t")

}
