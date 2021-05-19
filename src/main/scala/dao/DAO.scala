package dao

import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

trait DAO[T] {

  val db = DbConfig.db


  //  val table = TableQuery[_ <: Table[T] with IdentifiableTable]
  def table: TableQuery[Table[T] with IdentifiableTable]

  def add(t: T): Future[Int] = db.run(table += t)

  def delete(id: Int): Future[Int] = db.run(table.filter(_.id === id).delete)

  def get(): Future[Seq[T]] = db.run(table.result)

  def getLatest: Future[Option[T]] = db.run(table.sortBy(_.id.desc).result.headOption)
}



