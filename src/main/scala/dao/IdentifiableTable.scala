package dao

import slick.jdbc.PostgresProfile.api._

trait IdentifiableTable extends Table[Int] {
  def id = column[Int]("id")
}
