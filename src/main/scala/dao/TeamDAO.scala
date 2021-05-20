package dao

import models.Team
import slick.lifted.{TableQuery, Tag}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future


object TeamDAO extends DAO[Team] {

  class TeamTable(tag: Tag) extends Table[Team](tag, "team") with IdentifiableTable {
    //    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    override def * =
      (id, name) <> ((Team.apply _).tupled, Team.unapply)
  }

  override val table: TableQuery[TeamTable] = TableQuery[TeamTable]

  def getByName(name: String): Future[Team] = {
    db.run(table.filter(_.name === name).result.head)
  }
}

