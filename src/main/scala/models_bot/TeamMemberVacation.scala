package models_bot

import java.sql.Date

final case class TeamMemberVacation(id: Int, teamMemberId: Int, startDate: Date, endDate: Date, isApproved: Boolean)
