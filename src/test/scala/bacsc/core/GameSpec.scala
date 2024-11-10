package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec

import bacsc.MemRankingRepo


class GameSpec extends AnyFlatSpec:

  def dummyRoundFactory(notifyRoundEnd: () => Unit): Round =
    Round(BCNumber.draw(), DummyRoundOutputPort(), MemRankingRepo(), notifyRoundEnd)


  behavior of "Game"

  it should "be fresh after creation" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    assert(game.state == Game.Fresh)
    assert(!game.isOpen)
  }

  behavior of "Game (fresh)"

  it should "be closable" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.close()
    assert(game.state == Game.Closed)
  }

  it should "create fresh round after it is opened" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.open()
    assert(game.currentRound.isOpen)
  }

  behavior of "Game (opened)"

  it should "fetch new round after old is ended" in {
    var roundEnd: () => Unit = null
    def fakeRoundFactory(notifyRoundEnd: () => Unit): Round.Interface =
      roundEnd = notifyRoundEnd
      dummyRoundFactory(notifyRoundEnd)
    val game = Game(fakeRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.open()
    val oldRound = game.currentRound

    roundEnd()

    assert(game.currentRound != oldRound)
  }

  it should "present each fresh round" in {
    var lastPresentedRound: Round.Model = null
    val fakeGameOutputPort = new DummyGameOutputPort:
      override def presentRound(round: Round.Model): Unit =
        lastPresentedRound = round
    val game = Game(dummyRoundFactory, fakeGameOutputPort, MemRankingRepo())
    game.open()
    assert(lastPresentedRound == game.currentRound)
    game.restartRound()
    assert(lastPresentedRound == game.currentRound)
  }

  it should "close current round when closed" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.open()

    game.close()

    assert(!game.currentRound.isOpen)
  }

  it should "restart round" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.open()
    val oldRound = game.currentRound

    game.restartRound()

    assert(oldRound != game.currentRound)
  }

  it should "show introduction help message" in {
    var showedText: String = null
    var showedTitle: String = null
    val fakeGameOutputPort = new DummyGameOutputPort:
      override def pager(text: String, title: String): Unit =
        showedText = text
        showedTitle = title
    val game = Game(dummyRoundFactory, fakeGameOutputPort, MemRankingRepo())

    game.help(Game.HelpSubject.Introduction)

    assert(showedTitle == "Introduction")
    assert(
      showedText ==
        """Welcome in BacSc – "Bulls and Cows" implementation in Scala by Tomasz Olszewski.
          |
          |To get specific help type:
          |    :help [introduction]   - this help
          |    :help rules            - game rules
          |    :help :help            - how to use ':help' command
          |    :commands              - list available commands
          |""".stripMargin
    )
  }

  it should "show rules description" in {
    var showedText: String = null
    var showedTitle: String = null
    val fakeGameOutputPort = new DummyGameOutputPort:
      override def pager(text: String, title: String): Unit =
        showedText = text
        showedTitle = title
    val game = Game(dummyRoundFactory, fakeGameOutputPort, MemRankingRepo())

    game.help(Game.HelpSubject.Rules)

    assert(showedTitle == "Game rules")
    assert(showedText ==
      """* You have to guess number of which digits do not repeat.
        |* Enter your guess and program will return numbers of
        |  bulls (amount of digits that are correct and have
        |  correct position) and cows (amount of correct digits
        |  but with wrong position).
        |* Try to find correct number with fewest amount of
        |  attempts.
        |""".stripMargin
    )
  }

  it should "show help for command" in {
    object FakeCommand extends Command("fgh"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] = ???
      override def signature = ":fgh arg1 [arg2]"
      override def description = "Some fake command description\nin multiple lines."
    var showedText: String = null
    var showedTitle: String = null
    val fakeGameOutputPort = new DummyGameOutputPort:
      override def pager(text: String, title: String): Unit =
        showedText = text
        showedTitle = title
    val game = Game(dummyRoundFactory, fakeGameOutputPort, MemRankingRepo())

    game.help(FakeCommand)

    assert(showedTitle == ":fgh")
    assert(showedText ==
      """:fgh arg1 [arg2]
        |
        |    Some fake command description
        |    in multiple lines.
        |""".stripMargin
    )
  }

  it should "list available commands' signatures" in {
    var showedText: String = null
    var showedTitle: String = null
    object FakeGameOutputPort extends DummyGameOutputPort:
      override def pager(text: String, title: String): Unit =
        showedText = text
        showedTitle = title
    object FakeCommandA extends Command("aaa"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] = ???
      override def signature = ":aaa arg1 [arg2]"
    object FakeCommandB extends Command("bbb"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] = ???
    val game = Game(
      dummyRoundFactory, FakeGameOutputPort, MemRankingRepo(), CommandRepository(_, List(FakeCommandA, FakeCommandB))
    )

    game.listCommands()

    assert(showedTitle == "Commands")
    assert(showedText ==
      """:aaa arg1 [arg2]
        |:bbb <???>
        |""".stripMargin)
  }

  it should "show ranking" in {
    var wasRankingPresent = false
    val fakeGameOutputPort = new DummyGameOutputPort:
      override def presentRanking(ranking: Ranking): Unit =
        wasRankingPresent = true
    val game = Game(dummyRoundFactory, fakeGameOutputPort, MemRankingRepo())

    game.showRanking()

    assert(wasRankingPresent)
  }

  behavior of "Game (closed/exhausted)"

  it should "be not able to open" in {
    val game = Game(dummyRoundFactory, DummyGameOutputPort(), MemRankingRepo())
    game.close()

    game.open()

    assert(game.state == Game.Closed)
  }
