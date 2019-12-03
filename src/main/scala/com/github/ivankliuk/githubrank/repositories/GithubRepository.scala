package com.github.ivankliuk.githubrank.repositories

import cats.implicits._
import cats.{Applicative, Traverse}
import com.github.ivankliuk.githubrank.ErrorOr
import com.github.ivankliuk.githubrank.models.{Organization, Repository}
import com.github.ivankliuk.githubrank.utils.Transformations.flattenLeftList
import com.github.ivankliuk.githubrank.workers.Fetcher
import com.google.inject.ImplementedBy

/**
 * An abstract interface for communication with Github API.
 *
 * @param fetcher retrieves web pages using underlying client.
 */
@ImplementedBy(classOf[GithubRepositoryPlay])
abstract class GithubRepository[F[_] : Applicative, R](fetcher: Fetcher[F, R]) {

  /**
   * Returns a list of repositories of the organization.
   *
   * @param organization Github organization.
   * @return Effect with encapsulated list of the repository entities.
   */
  def getRepositoriesByOrg(organization: Organization): F[ErrorOr[List[R]]] =
    fetcher.fetchResponses(s"orgs/${organization.name}/repos")

  /**
   * Returns a list of contributors to the repository.
   *
   * @param repos Github repository.
   * @return Effect with encapsulated list of the repository entities.
   */
  def getContributorsByRepositories(repos: Repository*): F[ErrorOr[List[R]]] =
    Traverse[List].sequence {
      repos.map { r =>
        fetcher.fetchResponses(s"repos/${r.fullName}/contributors")
      }.toList
    }.map(flattenLeftList)

}
