package services

import cats.data.Validated.{Invalid, Valid}
import dao.{TeamMemberDAO, TeamMemberVacationDAO}
import models.TeamMemberVacation
import models.Types._

import java.sql.Date
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object VacationApplicationService {

  import utils.Validation._

  trait VacationService {
    def validateDate(startDate: String, endDate: String): Either[ErrorMessage, VacationBoundaries]

    def apply(startDate: Date, endDate: Date, username: Username): Either[ErrorMessage, TeamMemberVacation]

    def awaitConfirmCommand: Either[ErrorMessage, Unit]

    def confirm(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, SuccessMessage]
  }

  class VacationServiceImpl extends VacationService {
    override def validateDate(startDate: String, endDate: String): Either[ErrorMessage, VacationBoundaries] = {
      validate(startDate, endDate) match {
        case Valid(validVal) => Right(validVal)
        case Invalid(e) => Left(e.toString)
      }
    }

    override def apply(startDate: Date, endDate: Date, username: Username): Either[ErrorMessage, TeamMemberVacation] = {
      val teamMemberId = Await.result(TeamMemberDAO.getByUsername(username), Duration.Inf)
      val vacationId = Await.result(TeamMemberVacationDAO.getLatest, Duration.Inf) match {
        case Some(value) => value.id + 1
        case None => 1
      }
      Right(TeamMemberVacation(vacationId, teamMemberId.id, startDate, endDate, isApproved = false))
    }

    override def awaitConfirmCommand: Either[ErrorMessage, Unit] = ???

    override def confirm(teamMemberVacation: TeamMemberVacation): Either[ErrorMessage, SuccessMessage] = {
      Await.result(TeamMemberVacationDAO.add(teamMemberVacation), Duration.Inf) match {
        case 1 => Right("Application created successfully")
        case 0 => Left("Application wasn't created")
      }
    }
  }


  def makeApplication(service: VacationService, startDate: String, endDate: String, username: Username): Either[ErrorMessage, SuccessMessage] = {
    for {
      validDate <- service.validateDate(startDate, endDate)
      application <- service.apply(validDate.startDate, validDate.endDate, username)
      _ <- service.awaitConfirmCommand
      result <- service.confirm(application)
    } yield result
  }
}
