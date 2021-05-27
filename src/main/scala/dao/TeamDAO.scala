package dao

import models_bot.Team
import models_bot.Types.{ErrorMessage, TeamName}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object TeamDAO {

  class TeamTable(tag: Tag) extends Table[Team](tag, "team") {
    def id = column[Int]("id")

    def name = column[String]("name")

    override def * =
      (id, name) <> ((Team.apply _).tupled, Team.unapply)
  }

  val teams = TableQuery[TeamTable]
  val db = DbConfig.db

  def add(team: Team): String = {
    doMatch(db.run(teams += team))
  }

  def delete(id: Int): Future[Int] = db.run(teams.filter(_.id === id).delete)

  def get(): Seq[Team] = Await.result(db.run(teams.result), Duration.Inf)

  def getLatest: Int = Await.result(db.run(teams.sortBy(_.id.desc).result.headOption), Duration.Inf) match {
    case Some(x) => x.id + 1
    case None => 1
  }

  def getByName(name: String): Future[Team] = {
    db.run(teams.filter(_.name === name).result.head)
  }

  def getTeamsByUsername(username: String): Seq[Team] = Await.result(db.run {
    (for {
      teamMember <- TeamMemberDAO.teamsMembers.filter(_.username === username)
      teamMemberTeams <- teams.filter(_.id =!= teamMember.teamId)
    } yield teamMemberTeams).result
  }, Duration.Inf)

  private def doMatch(intToMatch: Future[Int]): String =
    Await.result(intToMatch, Duration.Inf) match {
    case 1 => "Team added successfully"
    case 0 => "There are some problems"
  }
}
