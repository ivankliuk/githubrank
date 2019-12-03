package com.github.ivankliuk.githubrank.utils

import com.github.ivankliuk.githubrank.Error
import play.api.libs.ws.WSResponse

object Transformations {

  val lastPageRegex = """[&?]page=(\d+)>; rel="last""".r
  val urlRegex = """http(.*?)[&?]page=""".r
  val errorMessage = "pagination URL parsing failed"

  def extractPaginatedUrls(response: WSResponse): Either[String, List[String]] =
    response.header("Link").map { links =>
      val page = for {
        baseUrl <- urlRegex.findFirstIn(links)
        lastPage <- lastPageRegex.findFirstMatchIn(links).map(_.group(1)).map(_.toInt)
      } yield (1 to lastPage).map(idx => baseUrl + idx.toString).toList

      page.toRight(errorMessage)
    }.getOrElse(Right(List.empty))

  def flattenLeft[A](parsed: List[Either[Error, A]]): Either[Error, List[A]] = {
    val errors = parsed.collect { case Left(value) => value }
    if (errors.isEmpty)
      Right(parsed.collect { case Right(value) => value })
    else
      Left(errors.head) // For simplicity reasons, return the first error only.
  }

  def flattenLeftList[A](parsed: List[Either[Error, List[A]]]): Either[Error, List[A]] =
    flattenLeft(parsed).map(_.flatten)

}
