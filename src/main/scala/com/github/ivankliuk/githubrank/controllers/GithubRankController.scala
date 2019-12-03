package com.github.ivankliuk.githubrank.controllers

import cats.data.EitherT
import com.github.ivankliuk.githubrank.errors.RepositoryJsonError
import com.github.ivankliuk.githubrank.models.Organization
import com.github.ivankliuk.githubrank.services.GithubService
import javax.inject._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.mvc._

@Singleton
class GithubRankController @Inject()(cc: ControllerComponents, gs: GithubService[Task, WSResponse])
  extends AbstractController(cc) {

  def contributors(orgName: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val org = new Organization(orgName)
      val stats = for {
        repos <- EitherT(gs.getRepositoriesByOrg(org))
        contrib <- EitherT(gs.getContributorsByRepositories(repos: _*))
      } yield gs.getStats(contrib)

      // We treat all errors received from Github API as `502 Bad Gateway`.
      // Errors occurred during the service operation (parsing, serialization etc.)
      // are treated as `500 Internal Server Error`
      stats.value.map {
        case Right(contributors) => Ok(Json.toJson(contributors))
        case Left(err: RepositoryJsonError) => BadGateway(err.message)
        case Left(err) => InternalServerError(err.message)
      }.runToFuture
  }
}
