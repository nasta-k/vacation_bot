package dao

import models_bot.TeamMember
import models_bot.Types.ErrorMessage
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object TeamMemberDAO {

  class TeamMemberTable(tag: Tag) extends Table[TeamMember](tag, "team_member") {
    def id = column[Int]("id")

    def username = column[String]("username")

    def isPm = column[Boolean]("is_pm")

    def teamId = column[Int]("team_id")

    def tgId = column[Int]("tg_id")

    override def * =
      (id, username, isPm, teamId, tgId) <> ((TeamMember.apply _).tupled, TeamMember.unapply)
  }

  val teamsMembers = TableQuery[TeamMemberTable]
  val db = DbConfig.db

  def add(teamMember: TeamMember): String =
    doMatch(db.run(teamsMembers += teamMember), "You were added to the team")

  def delete(id: Int): Future[Int] = db.run(teamsMembers.filter(_.id === id).delete)

  def get(): Future[Seq[TeamMember]] = db.run(teamsMembers.result)

  def getLatest: Int = {
    Await.result(db.run(teamsMembers.sortBy(_.id.desc).result.headOption), Duration.Inf) match {
      case Some(x) => x.id + 1
      case None => 1
    }
  }

  def updateIsPm(teamMember: TeamMember): String = {
    doMatch(db.run(teamsMembers.filter(_.id === teamMember.id).update(teamMember)), "You were appointed a PM")
  }

  def getById(id: Int): Future[Option[TeamMember]] = {
    db.run(teamsMembers.filter(_.id === id).result.headOption)
  }

  def getByUsername(username: String): Future[TeamMember] = {
    db.run(teamsMembers.filter(_.username === username).result.head)
  }

  def getPm(username: String): Option[TeamMember] = Await.result(db.run {
    (for {
      teamMember <- teamsMembers.filter(_.username === username)
      pm <- teamsMembers.filter(_.teamId === teamMember.teamId).filter(_.isPm === true)
    } yield pm).result.headOption
  }, Duration.Inf)

  val teams = TeamDAO.teams

  def getByTeamName(name: String): Future[Seq[TeamMember]] = db.run {
    (for {
      team <- teams.filter(_.name === name)
      teamMembers <- teamsMembers.filter(_.teamId === team.id)
    } yield teamMembers).result
  }

  def getByTeamAndUsername(username: String, teamName: String): Option[TeamMember] = Await.result(db.run {
    (for {
      team <- teams.filter(_.name === teamName)
      teamMember <- TeamMemberDAO.teamsMembers.filter(_.username === username).filter(_.teamId === team.id)
    } yield teamMember).result.headOption
  }, Duration.Inf)

  private def doMatch(intToMatch: Future[Int], successString: String): String =
    Await.result(intToMatch, Duration.Inf) match {
      case 1 => s"$successString successfully"
      case 0 => "There are some problems"
    }
}
