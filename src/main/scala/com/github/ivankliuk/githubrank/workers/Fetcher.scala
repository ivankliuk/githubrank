package com.github.ivankliuk.githubrank.workers

import cats.data.EitherT
import cats.implicits._
import cats.{Applicative, Monad, Traverse}
import com.google.inject.ImplementedBy
import com.github.ivankliuk.githubrank.instances._
import com.github.ivankliuk.githubrank.syntax._
import com.github.ivankliuk.githubrank.utils.Transformations.flattenLeft
import com.github.ivankliuk.githubrank.{Error, ErrorOr, URL}

/**
 * An abstract implementation to page fetching machinery.
 *
 * [[Fetcher]] is a web crawler per se. What it does:
 *
 * 1) Gets relative URL;
 * 2) Receives response from API;
 * 3) Extracts pagination URLs from it;
 * 4) Spawns workers to fetch the pagination URLs;
 * 5) Returns the list of responses.
 */
@ImplementedBy(classOf[FetcherPlay])
abstract class Fetcher[F[_] : Applicative : Monad, R] {

  private def spawnRequests(urls: List[String]): F[ErrorOr[List[R]]] =
    Traverse[List]
      .sequence(urls.map(request)).map(flattenLeft)

  /**
   * Trade-off between performance and design:
   * We perform preflight request to fetch total page count. This increments
   * GitHub API hit count by one. We get simpler implementation instead.
   */
  private def fetchPaginatedUrls(path: String): F[ErrorOr[List[String]]] = {
    val firstPageUrl = Fetcher.BaseUrl + path

    EitherT(request(firstPageUrl))
      .flatMap { resp =>
        EitherT[F, Error, List[String]] {
          Monad[F].pure {
            extractPaginatedUrls(resp)
            .map(firstPageUrl :: _)
            .leftMap(_.asRepositoryError)
          }
        }
      }.value
  }

  def extractPaginatedUrls(response: R): Either[String, List[String]]

  def request(url: String): F[ErrorOr[R]]

  def fetchResponses(url: URL): F[ErrorOr[List[R]]] = {
    for {
      urls <- EitherT(fetchPaginatedUrls(url))
      responses <- EitherT(spawnRequests(urls))
    } yield responses
    }.value

}

object Fetcher {

  val BaseUrl = s"https://api.github.com/"

}

