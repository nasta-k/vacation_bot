package commands

import canoe.api._
import canoe.methods.messages.SendMessage
import canoe.syntax.{command, _}
import services.ApprovalService

object ApprovalCommandsHandler {
  def requestApproval[F[_] : TelegramClient](approvalService: ApprovalService[F]): Scenario[F, Unit] = {
    for {
      msg <- Scenario.expect(command("request_approval"))
      pm <- Scenario.eval(approvalService.getPm(msg.from.get.username.get))
      vacation <- Scenario.eval(approvalService.getVacation(msg.from.get.username.get))
      _ <- Scenario.eval(SendMessage(pm.right.get.tgId, s"${msg.from.get.username.get} wants" +
        s" to go on vacation from ${vacation.startDate.toString} to${vacation.endDate.toString}\n" +
        "Do you approve? If you do, press /approve").call)
      _ <- Scenario.expect(command("approve"))
      approvalMessage <- Scenario.eval(approvalService.approve(vacation))
      _ <- Scenario.eval(msg.chat.send(approvalMessage))
    } yield ()
  }
}
