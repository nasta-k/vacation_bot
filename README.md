# Vacation bot

Bot is aimed to help in organizing the process of managing/planning vacations inside a team.

## Modules
  * basic connection configuration;
  * add team/appoint PM;
  * application for vacation;
  * request an approve;
  * show calendar.

## Description
### Basic
Configure environment, create base connection model for the app, create app in heroku and configure db.
### Team adding
Basic implementations implies admin creation of the team - one user can create the whole team listing the usernames.
More advanced implementation - user chooses the team to add him/herself.
### Application for vacation
User makes a request including dates in it and confirms.
### Team approval
User requests either team's or PM's approval. Once it comes, user will be notified, whether he/she can go on vacation.
### Calendar
Shows the vacations of the team.
## Function model
### Team adding
* addTeam(teamName: TeamName) type TeamName = String
* addTeamMember(username: Username) type Username = String - username format @username
* appointPM(username: Username)
Checks for existence
### Application for vacation
* apply(startDate: java.util.SQLDate, endDate: java.util.SQLDate)
* confirm()
### Team approval
* requestApproval(forTeamMember: Username)
* doApprove(forTeamMember: Username)
* doNotApprove(forTeamMember: Username)
### Calendar
* updateCalendar(forTeamMember: Username, startDate: java.util.SQLDate, endDate: java.util.SQLDate)
* showCalendar()
Shows the vacations of the team.
## Deadlines
Module | Deadline | Asignee
| ----- | :-: | :-: |
Basic | 17.05 23:59 | - 
Team adding | 18.05 23:59 | - 
Application for vacation | 19.05 23:59 | -
Team approval | 20.05 23:59 | -
Calendar | 21.05 23:59 (optional) | -
**All** | **22.05 23:59** | -
## Development requirements
* database tool - **slick**
* database schema 
![db scheme](https://github.com/nasta-k/vacation_bot/blob/main/img.png)
## Git requirements
Modules are developed in separate branches naming as follows basic, add_team, application, approval, calendar.
## Deployment requirements
Heroku running, Postgres intregration.
