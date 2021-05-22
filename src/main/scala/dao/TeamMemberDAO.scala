package dao

import models.TeamMember
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Future

object TeamMemberDAO {

  class TeamMemberTable(tag: Tag) extends Table[TeamMember](tag, "team_member") {
    def id = column[Int]("id")

    def username = column[String]("username")

    def isPM = column[Boolean]("is_pm")

    def teamId = column[Int]("team_id")

    override def * =
      (id, username, isPM, teamId) <> ((TeamMember.apply _).tupled, TeamMember.unapply)
  }

  val teamsMembers = TableQuery[TeamMemberTable]
  val db = DbConfig.db

  def add(teamMember: TeamMember): Future[Int] = db.run(teamsMembers += teamMember)

  def delete(id: Int): Future[Int] = db.run(teamsMembers.filter(_.id === id).delete)

  def get(): Future[Seq[TeamMember]] = db.run(teamsMembers.result)

  def getLatest: Future[Option[TeamMember]] = db.run(teamsMembers.sortBy(_.id.desc).result.headOption)

  def updateIsPM(teamMember: TeamMember): Future[Int] = {
    db.run(teamsMembers.filter(_.id === teamMember.id).update(teamMember))
  }

  def getById(id: Int): Future[Option[TeamMember]] = {
    db.run(teamsMembers.filter(_.id === id).result.headOption)
  }

  def getByUsername(username: String): Future[TeamMember] = {
    db.run(teamsMembers.filter(_.username === username).result.head)
  }

  def getPM(teamMemberId: Int): Future[Option[TeamMember]] = db.run {
    (for {
      teamMember <- teamsMembers.filter(_.id === teamMemberId)
      pm <- teamsMembers.filter(_.teamId === teamMember.teamId).filter(_.isPM === true)
    } yield pm).result.headOption
  }

  val teams = TeamDAO.teams

  def getByTeamName(name: String): Future[Seq[TeamMember]] = db.run {
    (for {
      team <- teams.filter(_.name === name)
      teamMembers <- teamsMembers.filter(_.teamId === team.id)
    } yield teamMembers).result
  }
}
