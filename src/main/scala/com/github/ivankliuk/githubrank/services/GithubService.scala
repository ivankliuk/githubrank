package com.github.ivankliuk.githubrank.services

import cats.Monad
import cats.implicits._
import com.github.ivankliuk.githubrank.ErrorOr
import com.github.ivankliuk.githubrank.models.{Contributor, Organization, Repository}
import com.github.ivankliuk.githubrank.repositories.GithubRepository
import com.github.ivankliuk.githubrank.utils.ResponseSerializer
import com.google.inject.ImplementedBy
import javax.inject.Inject


/**
 * An abstract implementation of [[GithubService]] operate with GithubRank
 * application abstractions.
 *
 * @param repository communicates with Github API.
 * @param serializer serializes responses.
 */
@ImplementedBy(classOf[GithubServicePlay])
class GithubService[F[_] : Monad, R] @Inject()(repository: GithubRepository[F, R], serializer: ResponseSerializer[R]) {

  def getRepositoriesByOrg(organization: Organization): F[ErrorOr[List[Repository]]] =
    repository.getRepositoriesByOrg(organization: Organization).map {
      _.flatMap(serializer.to[Repository])
    }

  def getContributorsByRepositories(repos: Repository*): F[ErrorOr[List[Contributor]]] =
    for {
      resp <- repository.getContributorsByRepositories(repos: _*)
      repos <- Monad[F].pure {
        resp.flatMap(serializer.to[Contributor])
      }
    } yield repos

  def getStats(contributors: List[Contributor]): List[Contributor] =
    contributors
      .groupMapReduce(_.login)(_.contributions)(_ + _)
      .toList
      .map(kv => Contributor(kv._1, kv._2))
      .sortBy(_.contributions)

}
