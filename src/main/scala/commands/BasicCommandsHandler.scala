package commands

import canoe.api._
import canoe.syntax._

object BasicCommandsHandler {
  private val startMessage: String = "Hi! You've just turned to vacation bot \uD83D\uDE0C \n" +
    "From now on you can plan your vacations throughout a team easily!\n" +
    "To learn details press /help"

  private val helpMessage: String = "Here are some commands:\n\n" +
    "/add_team - to add team\n" +
    "/add_member - to add you to the team\n" +
    "/appoint_pm - to appoint PM\n" +
    "/vacation - to apply for vacation\n" +
    "/confirm - to confirm vacation application\n" +
    "/request_approval - to request PM's approval for your application\n" +
    "/help - to get this message"

  def start[F[_] : TelegramClient]: Scenario[F, Unit] = {
    for {
      chat <- Scenario.expect(command("start").chat)
      _ <- Scenario.eval(chat.send(startMessage))
    } yield ()
  }

  def help[F[_] : TelegramClient]: Scenario[F, Unit] = {
    for {
      chat <- Scenario.expect(command("help").chat)
      _ <- Scenario.eval(chat.send(helpMessage))
    } yield ()
  }
}
