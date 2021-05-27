package models_bot

final case class TeamMember(id: Int, username: String, isPm: Boolean, teamId: Int, tgId: Int)
