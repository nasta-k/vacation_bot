package commands

import canoe.api._
import canoe.syntax._
import dao.TeamDAO
import services.TeamAddingService


object TeamAddingCommandsHandler {
  def getTeam(username: String):String = {
    val team = TeamDAO.get()
    team.map(x => s"${x.name}\n").mkString("")
  }

  def addTeam[F[_] : TelegramClient](addService: TeamAddingService[F]): Scenario[F, Unit] = {
    for {
      chat <- Scenario.expect(command("add_team").chat)
      _ <- Scenario.eval(chat.send("Type team's name"))
      name <- Scenario.expect(text)
      msg <- Scenario.eval(addService.addTeam(name))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()
  }

  def addMe[F[_] : TelegramClient](addService: TeamAddingService[F]): Scenario[F, Unit] = {
    for {
      msg <- Scenario.expect(command("add_member"))
      _ <- Scenario.eval(msg.chat.send(content = s"Choose team from which you're not a member of:\n${getTeam(msg.from.get.username.get)}"))
      team <- Scenario.expect(text)
      message <- Scenario.eval(addService.addTeamMember(msg.from.get.username.get, team, msg.from.get.id))
      _ <- Scenario.eval(msg.chat.send(message))
    } yield ()
  }

  def appointPm[F[_] : TelegramClient](addService: TeamAddingService[F]): Scenario[F, Unit] = {
    for {
      msg <- Scenario.expect(command("appoint_pm"))
      _ <- Scenario.eval(msg.chat.send("Type team name you need to be a PM of:"))
      team <- Scenario.expect(text)
      reply <- Scenario.eval(addService.appointPm(msg.from.get.username.get, team))
      _ <- Scenario.eval(msg.chat.send(reply))
    } yield ()
  }
}