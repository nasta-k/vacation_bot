package services

import cats.effect.IO
import dao.TeamMemberVacationDAO

trait CalendarService[F[_]] {
  def showCalendar(teamName: String): F[String]
}

object CalendarService {
  def apply(): CalendarService[IO] = (teamName: String) => {
    val calendarSeq = TeamMemberVacationDAO.getTeamCalendar(teamName)
    val calendar = calendarSeq.map(x => s"${x._1}: from ${x._2.toString} to ${x._3.toString}\n")
    IO(s"Vacation calendar for $teamName team:\n$calendar")
  }
}
