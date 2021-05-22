package services

import commands.TeamAddingHandler.getId
import dao._
import models._
import models.Types._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TeamAddService {

  trait TeamAddingService {
    def addTeam(teamName: TeamName): Either[ErrorMessage, Unit]

    def addTeamMember(username: Username, teamName: TeamName): Either[ErrorMessage, Unit]

    def appointPM(username: Username): Either[ErrorMessage, Unit]
  }

  class TeamAddingServiceImpl extends TeamAddingService {
    override def addTeam(teamName: TeamName): Either[ErrorMessage, Unit] = {
      doMatch(Await.result(TeamDAO.add(Team(getId(), teamName)), Duration.Inf))
    }

    override def addTeamMember(username: Username, teamName: TeamName): Either[ErrorMessage, Unit] = {
      val team = Await.result(TeamDAO.getByName(teamName), Duration.Inf)
      doMatch(Await.result(TeamMemberDAO.add(TeamMember(1, username, isPM = false, team.id)), Duration.Inf))
    }

    override def appointPM(username: Username): Either[ErrorMessage, Unit] = {
      val teamMember = Await.result(TeamMemberDAO.getByUsername(username), Duration.Inf)
      doMatch(Await.result(TeamMemberDAO.updateIsPM(teamMember.copy(isPM = true)), Duration.Inf))
    }

    def doMatch(intToMatch: Int): Either[ErrorMessage, Unit] = intToMatch match {
      case 1 => Right()
      case 0 => Left("It's not possible")
    }
  }
}
