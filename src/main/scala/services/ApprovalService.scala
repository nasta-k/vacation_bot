package services

import canoe.api.Scenario
import cats.effect.{IO, Sync}
import dao.{TeamMemberDAO, TeamMemberVacationDAO}
import models_bot._
import models_bot.Types._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait ApprovalService[F[_]] {
  def getPm(username: String): F[Either[String, TeamMember]]

  def getVacation(username: String): F[TeamMemberVacation]

  def approve(teamMemberVacation: TeamMemberVacation): F[String]
}

object ApprovalService {
  def apply(): ApprovalService[IO] = new ApprovalService[IO] {
    override def getPm(username: String): IO[Either[String, TeamMember]] =
      TeamMemberDAO.getPm(username) match {
        case Some(pm) => IO(Right(pm))
        case None => IO(Left("There is no PM for the team"))
      }

    override def approve(teamMemberVacation: TeamMemberVacation): IO[String] = {
      IO(TeamMemberVacationDAO.updateIsApproved(teamMemberVacation))
    }

    override def getVacation(username: String): IO[TeamMemberVacation] =
      IO(TeamMemberVacationDAO.getByUsername(username))
  }
}

//object ApprovalService {
//
//  trait PMApprovalService {
//    def getTeamMember(id: Int): Either[ErrorMessage, TeamMember]
//
//    def getPM(teamMemberId: Int): Either[ErrorMessage, TeamMember]
//
//    def send(PMUsername: Username, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Unit]
//
//    def getReply: Either[ErrorMessage, TeamMemberVacation]
//
//    def updateApplication(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int]
//  }
//
//  class PMApprovalServiceImpl extends PMApprovalService {
//    override def getTeamMember(id: Int): Either[ErrorMessage, TeamMember] = {
//      Await.result(TeamMemberDAO.getById(id), Duration.Inf) match {
//        case Some(teamMember) => Right(teamMember)
//        case None => Left("There is no team member for the vacation")
//      }
//    }
//
//    override def getPM(teamMemberId: Int): Either[ErrorMessage, TeamMember] = {
//      Await.result(TeamMemberDAO.getPM(teamMemberId), Duration.Inf) match {
//        case Some(pm) => Right(pm)
//        case None => Left("There is no PM in the team")
//      }
//    }
//
//    override def send(PMUsername: Username, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Unit] = {
//      Scenario.eval(chat.send(s"${teamMemberVacation.startDate}-${teamMemberVacation.endDate}"))
//    }
//
//    override def getReply: Either[ErrorMessage, TeamMemberVacation] = ???
//
//    override def updateApplication(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int] = {
//      Right(Await.result(TeamMemberVacationDAO.updateIsApproved(teamMemberVacation), Duration.Inf))
//    }
//  }
//
//  def getPMApproval(service: PMApprovalService, teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, Int] = {
//    for {
//      teamMember <- service.getTeamMember(teamMemberVacation.teamMemberId)
//      pm <- service.getPM(teamMember.id)
//      _ <- Scenario.eval(chat.send("Hello. What's your name?"))
//      _ <- service.send(pm.username, teamMemberVacation)
//      reply <- service.getReply
//      result <- service.updateApplication(reply)
//    } yield result
//  }
//}
