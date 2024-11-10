package bacsc.core


object CommandRepository:
  val DEFAULT_COMMANDS = List(QuitGame, RestartRound, GetHelp, ListCommands, ShowRanking)

class CommandRepository(game: Game.Interface, commands: List[Command] = CommandRepository.DEFAULT_COMMANDS):
  import Command.*

  val commandsMap = Map[String, Command](commands.map(cmd => (cmd.name, cmd))*)

  def executeCommandLine(line: String): Either[Exception, Unit] =
    line.split("\\s+").filter(_.nonEmpty) match
      case Array() =>
        Left(EmptyCommandLine)
      case Array(name, args*) =>
        getByAbbreviation(name)
          .flatMap(_.execute(game, args.toList))

  def getByAbbreviation(name: String): Either[NotFound | AmbiguousAbbreviation, Command] =
    get(name).left.flatMap(
      exception =>
        commandsMap.keys.toList.filter(_.startsWith(name)) match
          case List() => Left(exception)
          case List(name) => Right(commandsMap(name))
          case possibilities => Left(AmbiguousAbbreviation(name, possibilities))
    )

  def get(name: String): Either[NotFound, Command] =
    commandsMap.get(name)
      .toRight(NotFound(name))
