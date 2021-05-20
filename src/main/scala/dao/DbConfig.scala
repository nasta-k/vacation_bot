package dao

import slick.jdbc.JdbcBackend.Database

object DbConfig {
  private val host = "ec2-107-20-153-39.compute-1.amazonaws.com"
  private val database = "dagq27qgn5ob9c"
  private val user = "ujvoyhmwxcguqm"
  private val password = "8c977d6ea738086377ebee83e9966bf536c24ad76cc583ea332b700336fc826f"
  private val connectionUrl = s"jdbc:postgresql://$host/$database?user=$user&password=$password"

  val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
}
