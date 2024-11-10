package bacsc.core

export bacsc.MemRankingRepo


class DummyRoundOutputPort extends Round.OutputPort:
  override def presentGuessHints(guess: BCNumber, bullsCows: (Int, Int)): Unit = ()
  override def presentSuccessMessage(stepsDone: Int): Unit = ()
  override def promptPlayerNameToSaveScore(): Option[Player] = None
  override def presentUpdatedRanking(ranking: Ranking, position: Int): Unit = ()

class RoundStub extends Round.Interface:
  override def step: Int = ???
  override def isOpen: Boolean = ???
  override def makeGuess(guess: BCNumber): Unit = ???
  override def close(): Unit = ???


class DummyGameOutputPort extends Game.OutputPort:
  override def pager(text: String, title: String): Unit = ()
  override def presentRound(round: Round.Model): Unit = ()
  override def presentRanking(ranking: Ranking): Unit = ()

class GameStub extends Game.Interface:
  override def state: Game.State = ???
  override def currentRound: Round.Model = ???
  override def close(): Unit = ???
  override def restartRound(): Unit = ???
  override def commandRepository: CommandRepository = ???
  override def help(subject: Game.HelpSubject | Command): Unit = ???
  override def listCommands(): Unit = ???
  override def showRanking(): Unit = ???
