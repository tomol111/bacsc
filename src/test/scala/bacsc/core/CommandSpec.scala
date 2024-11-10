package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec


class CommandSpec extends AnyFlatSpec:

  behavior of "Command exception renderer"

  it should "render - empty command line - message" in {
    val result = Command.EmptyCommandLine.render()
    assert(result == "Command line was empty")
  }

  it should "render - command not found - message" in {
    val result = Command.NotFound("xyz").render()
    assert(result == "Command 'xyz' has not been found")
  }

  it should "render - ambiguous abbreviation - message" in {
    val result = Command.AmbiguousAbbreviation("ab", List("abcd", "abxy")).render()
    assert(result == "Ambiguous abbreviation 'ab'. Possibilities: abcd, abxy")
  }

  it should "render - argument exception - message" in {
    val result = Command.ArgumentException("Some message").render()
    assert(result == "Some message")
  }


  behavior of "QuitGame"

  it should "close game" in {
    var wasGameClosed = false
    val fakeGame = new GameStub:
      override def close(): Unit =
        wasGameClosed = true

    QuitGame.execute(fakeGame, List())

    assert(wasGameClosed)
  }

  it should "take no arguments" in {
    val fakeGame = new GameStub:
      override def close(): Unit = ()
    val result = QuitGame.execute(fakeGame, List("arg"))
    assert(result == Left(Command.ArgumentException("No arguments expected")))
  }


  behavior of "RestartRound"

  it should "restart round" in {
    var wasRoundRestarted = false
    val fakeGame = new GameStub:
      override def restartRound(): Unit =
        wasRoundRestarted = true

    RestartRound.execute(fakeGame, List())

    assert(wasRoundRestarted)
  }

  it should "take no arguments" in {
    val fakeGame = new GameStub:
      override def restartRound(): Unit = ()
    val result = RestartRound.execute(fakeGame, List("arg"))
    assert(result == Left(Command.ArgumentException("No arguments expected")))
  }


  behavior of "GetHelp"

  it should "give introduction help when no subject was given" in {
    var givenSubject: Game.HelpSubject | Command = null
    val fakeGame = new GameStub:
      override def help(subject: Game.HelpSubject | Command): Unit =
        givenSubject = subject

    GetHelp.execute(fakeGame, List())

    assert(givenSubject == Game.HelpSubject.Introduction)
  }

  it should "give help about subject" in {
    var givenSubject: Game.HelpSubject | Command= null
    val fakeGame = new GameStub:
      override def help(subject: Game.HelpSubject | Command): Unit =
        givenSubject = subject

    GetHelp.execute(fakeGame, List("introduction"))
    assert(givenSubject == Game.HelpSubject.Introduction)

    GetHelp.execute(fakeGame, List("rules"))
    assert(givenSubject == Game.HelpSubject.Rules)
  }

  it should "inform about invalid subject" in {
    val result = GetHelp.execute(GameStub(), List("invalid"))
    assert(result == Left(Command.ArgumentException("Non available subject 'invalid'")))
  }

  it should "not take more than one argument" in {
    val result = GetHelp.execute(GameStub(), List("subject", "additional_argument"))
    assert(result == Left(Command.ArgumentException("At most 1 argument expected but got 2")))
  }

  it should "give help for command" in {
    object FakeCommand extends Command("xyz"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] = ???
    var givenSubject: Game.HelpSubject | Command = null
    val fakeGame = new GameStub:
      override def help(subject: Game.HelpSubject | Command): Unit =
        givenSubject = subject
      override def commandRepository: CommandRepository = CommandRepository(this, List(FakeCommand))

    GetHelp.execute(fakeGame, List(":xyz"))

    assert(givenSubject == FakeCommand)
  }

  behavior of "ListCommands"

  it should "list available commands" in {
    var wasCommandsListed = false
    val fakeGame = new GameStub:
      override def listCommands(): Unit =
        wasCommandsListed = true

    ListCommands.execute(fakeGame, List())

    assert(wasCommandsListed)
  }

  it should "take no arguments" in {
    val fakeGame = new GameStub:
      override def listCommands(): Unit = ()
    val result = ListCommands.execute(fakeGame, List("arg"))
    assert(result == Left(Command.ArgumentException("No arguments expected")))
  }

  behavior of "ShowRanking"

  it should "show ranking" in {
    var wasRankingShowed = false
    val fakeGame = new GameStub:
      override def showRanking(): Unit =
        wasRankingShowed = true

    ShowRanking.execute(fakeGame, List())

    assert(wasRankingShowed)
  }

  it should "take no arguments" in {
    val fakeGame = new GameStub:
      override def listCommands(): Unit = ()
    val result = ShowRanking.execute(fakeGame, List("arg"))
    assert(result == Left(Command.ArgumentException("No arguments expected")))
  }

