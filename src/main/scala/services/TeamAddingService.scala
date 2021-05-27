package services

import cats.effect.IO
import dao.{TeamDAO, TeamMemberDAO}
import models_bot.{Team, TeamMember}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait TeamAddingService[F[_]] {
  def addTeam(teamName: String): F[String]

  def addTeamMember(username: String, teamName: String, userId: Int): F[String]

  def appointPm(username: String, teamName: String): F[String]
}
object TeamAddingService{
  def apply(): TeamAddingService[IO] = new TeamAddingService[IO]{
    override def addTeam(teamName: String): IO[String] = IO(TeamDAO.add(Team(TeamDAO.getLatest, teamName)))

    override def addTeamMember(username: String, teamName: String, userId: Int): IO[String] = {
      val team = Await.result(TeamDAO.getByName(teamName), Duration.Inf)
      IO(TeamMemberDAO.add(TeamMember(TeamMemberDAO.getLatest, username, isPm = false, team.id, userId)))
    }

    override def appointPm(username: String, teamName: String): IO[String] = {
      val teamMember = TeamMemberDAO.getByTeamAndUsername(username, teamName)
      teamMember match {
        case Some(tm) =>
          IO(TeamMemberDAO.updateIsPm(tm.copy(isPm = true)))
        case None => IO(s"You can not be a PM of $teamName team")
      }
    }
  }
}