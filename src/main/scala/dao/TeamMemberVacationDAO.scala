package dao

import models_bot.TeamMemberVacation
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import java.sql.Date
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object TeamMemberVacationDAO {

  class TeamMemberVacationTable(tag: Tag) extends Table[TeamMemberVacation](tag, "team_member_vacation") {
    def id = column[Int]("id")

    def teamMemberId = column[Int]("team_member_id")

    def startDate = column[Date]("start_date")

    def endDate = column[Date]("end_date")

    def isApproved = column[Boolean]("is_approved")

    override def * =
      (id, teamMemberId, startDate, endDate, isApproved) <> ((TeamMemberVacation.apply _).tupled, TeamMemberVacation.unapply)
  }

  val teamsMembersVacations = TableQuery[TeamMemberVacationTable]
  val db = DbConfig.db

  def add(teamMemberVacation: TeamMemberVacation): String =
    doMatch(db.run(teamsMembersVacations += teamMemberVacation), "You added successfully")

  def delete(id: Int): Future[Int] = db.run(teamsMembersVacations.filter(_.id === id).delete)

  def get(): Future[Seq[TeamMemberVacation]] = db.run(teamsMembersVacations.result)

  def getLatest: Int = {
    Await.result(db.run(teamsMembersVacations.sortBy(_.id.desc).result.headOption), Duration.Inf) match {
      case Some(x) => x.id + 1
      case None => 1
    }
  }

  def getByUsername(username: String): TeamMemberVacation = Await.result(db.run {
    (for {
      teamMember <- TeamMemberDAO.teamsMembers.filter(_.username === username)
      vacation <- teamsMembersVacations.filter(_.teamMemberId === teamMember.id)
    } yield vacation).result.head
  }, Duration.Inf)

  def updateIsApproved(teamMemberVacation: TeamMemberVacation): String = {
    doMatch(db.run(teamsMembersVacations.filter(_.id === teamMemberVacation.id).update(teamMemberVacation)), "You got an approval for your vacation")
  }

  def getTeamCalendar(teamName: String): Seq[(String, Date, Date)] = Await.result(db.run {
    (for {
      team <- TeamDAO.teams.filter(_.name === teamName)
      teamMembers <- TeamMemberDAO.teamsMembers.filter(_.teamId === team.id)
      vacation <- teamsMembersVacations.filter(_.teamMemberId === teamMembers.id)
    } yield (teamMembers.username, vacation.startDate, vacation.endDate)).result
  }, Duration.Inf)

  private def doMatch(intToMatch: Future[Int], successString: String): String =
    Await.result(intToMatch, Duration.Inf) match {
      case 1 => successString
      case 0 => "There are some problems"
    }
}
