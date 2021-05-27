import canoe.api._
import cats.effect.{ExitCode, IO, IOApp}
import commands.ApprovalCommandsHandler._
import commands.BasicCommandsHandler._
import commands.TeamAddingCommandsHandler._
import commands.VacationApplicationCommandsHandler._
import fs2.Stream
import services.{ApprovalService, TeamAddingService, VacationApplicationService}


object Main extends IOApp {
  val token: String = "1808776632:AAG5r-B5HKy28UsAu6EkZ6ZIPoanH6rX86c"
  val addService: TeamAddingService[IO] = TeamAddingService()
  val vacationService: VacationApplicationService[IO] = VacationApplicationService()
  val approvalService: ApprovalService[IO] = ApprovalService()

  override def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(start, help, addTeam(addService), showTeamsKeyboard, appointPm(addService),
          apply(vacationService), requestApproval(approvalService))
      }
      .compile.drain.as(ExitCode.Success)
}
