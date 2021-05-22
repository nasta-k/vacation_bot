package models

import java.sql.Date

case class TeamMemberVacation(id: Int, teamMemberId: Int, startDate: Date, endDate: Date, isApproved: Boolean)
