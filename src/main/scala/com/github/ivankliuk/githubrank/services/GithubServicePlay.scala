package com.github.ivankliuk.githubrank.services

import com.github.ivankliuk.githubrank.repositories.GithubRepositoryPlay
import com.github.ivankliuk.githubrank.utils.WSResponseSerializer
import javax.inject.Inject
import monix.eval.Task
import play.api.libs.ws.WSResponse


/**
 * Concrete implementation of [[GithubService]] to work with PlayFramework classes.
 *
 * @param repository communicates with Github API via PlayFramework tools.
 * @param serializer serializes HTTP responses from WSClient.
 */
class GithubServicePlay @Inject()(repository: GithubRepositoryPlay, serializer: WSResponseSerializer)
  extends GithubService[Task, WSResponse](repository, serializer)
