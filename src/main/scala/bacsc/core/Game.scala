package bacsc.core


object Game:
  enum State:
    case Fresh, Opened, Closed
  export State.*

  trait Model:
    def state: State
    def isOpen: Boolean = state == Opened
    def currentRound: Round.Model

  trait Interface extends Model:
    def commandRepository: CommandRepository
    def close(): Unit
    def restartRound(): Unit
    def help(subject: HelpSubject | Command): Unit
    def listCommands(): Unit
    def showRanking(): Unit

  trait OutputPort:
    def pager(text: String, title: String = ""): Unit
    def presentRound(round: Round.Model): Unit
    def presentRanking(ranking: Ranking): Unit


  enum HelpSubject:
    case Introduction, Rules

  val INTRODUCTION_MESSAGE =
    """Welcome in BacSc – "Bulls and Cows" implementation in Scala by Tomasz Olszewski.
      |
      |To get specific help type:
      |    :help [introduction]   - this help
      |    :help rules            - game rules
      |    :help :help            - how to use ':help' command
      |    :commands              - list available commands
      |""".stripMargin

  val RULES_DESCRIPTION =
    """* You have to guess number of which digits do not repeat.
      |* Enter your guess and program will return numbers of
      |  bulls (amount of digits that are correct and have
      |  correct position) and cows (amount of correct digits
      |  but with wrong position).
      |* Try to find correct number with fewest amount of
      |  attempts.
      |""".stripMargin

class Game(
    roundFactory: (() => Unit) => Round.Interface,
    outputPort: Game.OutputPort,
    rankingRepo: RankingRepo,
    commandRepositoryFactory: Game.Interface => CommandRepository = CommandRepository(_),
) extends Game.Interface:
  import Game.*
  private var _state: State = Fresh
  private var _currentRound: Option[Round.Interface] = None

  val commandRepository = commandRepositoryFactory(this)

  def state: State = _state

  def open(): Unit =
    if state == Fresh then
      _state = Opened
      fetchRound()

  def close(): Unit =
    _state = Closed
    _currentRound.foreach(_.close())

  def currentRound: Round.Model = _currentRound.get

  private def fetchRound(): Unit =
    if isOpen then
      _currentRound = Some(roundFactory(fetchRound))
      outputPort.presentRound(currentRound)

  def restartRound(): Unit = _currentRound.get.close()


  def help(subject: HelpSubject | Command): Unit =
    import HelpSubject.*
    val (text, title) = subject match
      case Introduction => (INTRODUCTION_MESSAGE, "Introduction")
      case Rules => (RULES_DESCRIPTION, "Game rules")
      case command: Command => (getCommandHelp(command), Command.PREFIX + command.name)
    outputPort.pager(text, title)

  private def getCommandHelp(command: Command): String =
    val indentation = 4
    val indentedDescription = command.description.linesWithSeparators
      .map(_.prependedAll(" " * indentation))
      .mkString
    s"""${command.signature}
       |
       |${indentedDescription.stripLineEnd}
       |""".stripMargin

  def listCommands(): Unit =
    val commandList = commandRepository.commandsMap.values.map(_.signature + "\n").mkString
    outputPort.pager(commandList, "Commands")

  def showRanking(): Unit =
    outputPort.presentRanking(rankingRepo.getTop())
