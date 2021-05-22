package dao

import models.TeamMemberVacation
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import java.sql.Date
import scala.concurrent.Future

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

  def add(teamMemberVacation: TeamMemberVacation): Future[Int] = db.run(teamsMembersVacations += teamMemberVacation)

  def delete(id: Int): Future[Int] = db.run(teamsMembersVacations.filter(_.id === id).delete)

  def get(): Future[Seq[TeamMemberVacation]] = db.run(teamsMembersVacations.result)

  def getLatest: Future[Option[TeamMemberVacation]] = db.run(teamsMembersVacations.sortBy(_.id.desc).result.headOption)

  def getById(id: Int): Future[TeamMemberVacation] = {
    db.run(teamsMembersVacations.filter(_.id === id).result.head)
  }

  def updateIsApproved(teamMemberVacation: TeamMemberVacation): Future[Int] = {
    db.run(teamsMembersVacations.filter(_.id === teamMemberVacation.id).update(teamMemberVacation))
  }

  def getTeamCalendar(username: String): Future[Seq[(String, Date, Date)]] = db.run {
    (for {
      user <- TeamMemberDAO.teamsMembers.filter(_.username === username)
      teamMembers <- TeamMemberDAO.teamsMembers.filter(_.teamId === user.teamId)
      vacation <- teamsMembersVacations.filter(_.teamMemberId === teamMembers.id)
    } yield (username, vacation.startDate, vacation.endDate)).result
  }
}
