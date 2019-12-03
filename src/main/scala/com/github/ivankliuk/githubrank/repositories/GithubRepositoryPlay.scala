package com.github.ivankliuk.githubrank.repositories

import javax.inject.Inject
import com.github.ivankliuk.githubrank.workers.FetcherPlay

/**
 * Concrete implementation of [[com.github.ivankliuk.githubrank.repositories.GithubRepository]]
 * to operate with [[com.github.ivankliuk.githubrank.workers.FetcherPlay]].
 *
 * @param fetcher operates with [[monix.eval.Task]] and
 *               [[play.api.libs.ws.WSResponse]] as a return value.
 */
class GithubRepositoryPlay @Inject()(fetcher: FetcherPlay)
  extends GithubRepository(fetcher)

