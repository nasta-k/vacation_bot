package services

import dao.{TeamMemberDAO, TeamMemberVacationDAO}
import models._
import models.Types._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ApprovalService {

  trait PMApprovalService {
    def getTeamMember(id: Int): Either[ErrorMessage, TeamMember]

    def getPM(teamMemberId: Int): Either[ErrorMessage, TeamMember]

    def send(PMUsername: Username, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Unit]

    def getReply: Either[ErrorMessage, TeamMemberVacation]

    def updateApplication(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int]
  }

  class PMApprovalServiceImpl extends PMApprovalService {
    override def getTeamMember(id: Int): Either[ErrorMessage, TeamMember] = {
      Await.result(TeamMemberDAO.getById(id), Duration.Inf) match {
        case Some(teamMember) => Right(teamMember)
        case None => Left("There is no team member for the vacation")
      }
    }

    override def getPM(teamMemberId: Int): Either[ErrorMessage, TeamMember] = {
      Await.result(TeamMemberDAO.getPM(teamMemberId), Duration.Inf) match {
        case Some(pm) => Right(pm)
        case None => Left("There is no PM in the team")
      }
    }

    override def send(PMUsername: Username, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Unit] = ???

    override def getReply: Either[ErrorMessage, TeamMemberVacation] = ???

    override def updateApplication(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int] = {
      Right(Await.result(TeamMemberVacationDAO.updateIsApproved(teamMemberVacation), Duration.Inf))
    }
  }

  def getPMApproval(service: PMApprovalService, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int] = {
    for {
      teamMember <- service.getTeamMember(teamMemberVacation.teamMemberId)
      pm <- service.getPM(teamMember.id)
      _ <- service.send(pm.username, teamMemberVacation)
      reply <- service.getReply
      result <- service.updateApplication(reply)
    } yield result
  }
}
