package commands

import canoe.api._
import canoe.syntax._
import services.VacationApplicationService

object VacationApplicationCommandsHandler {
  def apply[F[_] : TelegramClient](vacationApplicationService: VacationApplicationService[F]): Scenario[F, Unit] = {
    for {
      msg <- Scenario.expect(command("vacation"))
      _ <- Scenario.eval(msg.chat.send("Type dates for your vacation in format '%Y-%m-%d %Y-%m-%d'"))
      datesString <- Scenario.expect(text)
      dates = datesString.text.split(" ")
      vacation <- Scenario.eval(vacationApplicationService.apply(dates.head, dates(1), msg.from.get.username.get))
      _ <- Scenario.eval(msg.chat.send("Press /confirm if everything is correct"))
      _ <- Scenario.expect(command("confirm"))
      reply <- Scenario.eval(vacationApplicationService.confirm(vacation.right.get))
      _ <- Scenario.eval(msg.chat.send(reply))
    } yield ()
  }
}
