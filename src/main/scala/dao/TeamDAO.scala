package dao

import models.Team
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Future

object TeamDAO {

  class TeamTable(tag: Tag) extends Table[Team](tag, "team") {
    def id = column[Int]("id")

    def name = column[String]("name")

    override def * =
      (id, name) <> ((Team.apply _).tupled, Team.unapply)
  }

  val teams = TableQuery[TeamTable]
  val db = DbConfig.db

  def add(team: Team): Future[Int] = db.run(teams += team)

  def delete(id: Int): Future[Int] = db.run(teams.filter(_.id === id).delete)

  def get(): Future[Seq[Team]] = db.run(teams.result)

  def getLatest: Future[Option[Team]] = db.run(teams.sortBy(_.id.desc).result.headOption)

  def getByName(name: String): Future[Team] = {
    db.run(teams.filter(_.name === name).result.head)
  }
}
