import org.latestbit.slack.morphism.client.reqresp.test.{SlackApiTestRequest, SlackApiTestResponse}
import org.latestbit.slack.morphism.client._
import org.latestbit.slack.morphism.common.SlackAccessTokenValue

import cats.effect._

import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend

class Client {
  implicit val slackApiToken: SlackApiToken = SlackApiBotToken(SlackAccessTokenValue(""))
  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)

  def client: IO[Either[SlackApiClientError, SlackApiTestResponse]] = {
    for {
      backend <- AsyncHttpClientCatsBackend[IO]()
      client = SlackApiClient.build[IO](backend).create()
      result <- client.api.test(SlackApiTestRequest())
    } yield result
  }
}
