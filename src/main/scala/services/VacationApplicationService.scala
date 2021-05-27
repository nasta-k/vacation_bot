package services

import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import dao.{TeamMemberDAO, TeamMemberVacationDAO}
import models_bot.TeamMemberVacation
import models_bot.Types.ErrorMessage
import utils.Validation.{VacationBoundaries, validate}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait VacationApplicationService[F[_]] {
  def apply(startDate: String, endDate: String, username: String): F[Either[String, TeamMemberVacation]]

  def confirm(teamMemberVacation: TeamMemberVacation): F[String]
}

object VacationApplicationService {
  def apply(): VacationApplicationService[IO] = new VacationApplicationService[IO] {
    override def apply(startDate: String, endDate: String, username: String): IO[Either[String, TeamMemberVacation]] = {
      val validated = validateDate(startDate, endDate)
      if (validated.isRight) {
        val teamMemberId = Await.result(TeamMemberDAO.getByUsername(username), Duration.Inf)
        IO(Right(TeamMemberVacation(TeamMemberVacationDAO.getLatest, teamMemberId.id,validated.right.get.startDate, validated.right.get.endDate, isApproved = false)))
      }
      else IO(Left(validated.left.get))
    }

    override def confirm(teamMemberVacation: TeamMemberVacation): IO[String] = IO(TeamMemberVacationDAO.add(teamMemberVacation))

    def validateDate(startDate: String, endDate: String): Either[ErrorMessage, VacationBoundaries] = {
      validate(startDate, endDate) match {
        case Valid(validVal) => Right(validVal)
        case Invalid(e) => Left(e.toString)
      }
    }
  }
}
