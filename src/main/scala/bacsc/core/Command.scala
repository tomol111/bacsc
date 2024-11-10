package bacsc.core


abstract class Command(val name: String):
  def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit]
  def signature: String = s"$noArgsSignature <???>"
  protected def noArgsSignature: String = s"${Command.PREFIX}$name"
  def description: String = ""

object Command:
  val PREFIX = ':'

  export Exception.*
  enum Exception:
    case EmptyCommandLine
    case NotFound(name: String)
    case AmbiguousAbbreviation(abbreviation: String, possibilities: List[String])
    case ArgumentException(message: String)

    def render(): String = this match
      case EmptyCommandLine =>
        "Command line was empty"
      case NotFound(name) =>
        s"Command '$name' has not been found"
      case AmbiguousAbbreviation(name, possibilities) =>
        s"Ambiguous abbreviation '$name'. Possibilities: " + possibilities.mkString(", ")
      case ArgumentException(message) =>
        message

object QuitGame extends QuitGame
class QuitGame extends Command("quit"):
  def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
    if args.nonEmpty
      then Left(Command.ArgumentException("No arguments expected"))
      else Right(game.close())

  override def signature: String = noArgsSignature
  override def description: String = "Quit game"


object RestartRound extends RestartRound
class RestartRound extends Command("restart"):
  def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
    if args.nonEmpty
      then Left(Command.ArgumentException("No arguments expected"))
      else Right(game.restartRound())

  override def signature: String = noArgsSignature
  override def description: String = "Restart round"


object GetHelp extends GetHelp:
  val DEFAULT_SUBJECT = Game.HelpSubject.Introduction
  val subjectMap: Map[String, Game.HelpSubject] = Map.from(
    for subject <- Game.HelpSubject.values yield (subject.toString.map(_.toLower), subject)
  )
class GetHelp extends Command("help"):
  import GetHelp.*
  def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
    val subject = args match
      case List() =>
        Right(DEFAULT_SUBJECT)
      case List(subject) if subject.startsWith(Command.PREFIX.toString) =>
        game.commandRepository.getByAbbreviation(subject stripPrefix Command.PREFIX.toString)
      case List(subject) =>
        subjectMap.get(subject)
          .toRight(Command.ArgumentException(s"Non available subject '$subject'"))
      case List(args*) =>
        Left(Command.ArgumentException(s"At most 1 argument expected but got ${args.length}"))
    subject.map(game.help)

  override def signature: String = s"$noArgsSignature [subject]"

  override def description: String =
    """Get help on subject where subject can be:
      |    * introduction  - short program introduction (default)
      |    * rules         - describe game rules
      |or it can be command prefixed with ':' character.
      |""".stripMargin


object ListCommands extends ListCommands
class ListCommands extends Command("commands"):
  override def signature: String = noArgsSignature
  override def description: String = "List available commands"

  override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
    if args.nonEmpty
      then Left(Command.ArgumentException("No arguments expected"))
      else Right(game.listCommands())

object ShowRanking extends ShowRanking
class ShowRanking extends Command("ranking"):
  override def signature: String = noArgsSignature
  override def description: String = "Show ranking top"

  override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
    if args.nonEmpty
      then Left(Command.ArgumentException("No arguments expected"))
      else Right(game.showRanking())
