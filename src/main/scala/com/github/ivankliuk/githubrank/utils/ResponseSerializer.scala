package com.github.ivankliuk.githubrank.utils

import com.github.ivankliuk.githubrank.{ErrorOr, ReadsList}

/**
 * An interface to serializer for response body obtained from underlying client.
 *
 * @tparam R response to be serialized
 */
trait ResponseSerializer[R] {

  def to[A : ReadsList](responses: List[R]): ErrorOr[List[A]]

}
