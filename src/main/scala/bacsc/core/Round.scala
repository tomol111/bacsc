package bacsc.core


object Round:
  trait Model:
    def step: Int
    def isOpen: Boolean
    def makeGuess(guess: BCNumber): Unit  // ToDo: maybe move to RoundInterface

  case object ClosedException extends Exception

  trait Interface extends Round.Model:
    def close(): Unit

  trait OutputPort:
    def presentGuessHints(guess: BCNumber, bullsCows: (Int, Int)): Unit
    def presentSuccessMessage(stepsDone: Int): Unit
    def promptPlayerNameToSaveScore(): Option[Player]
    def presentUpdatedRanking(ranking: Ranking, position: Int): Unit

class Round(
    secret: BCNumber,
    outputPort: Round.OutputPort,
    rankingRepo: RankingRepo,
    notifyRoundEnd: () => Unit,
) extends Round.Interface:
  import Round.*

  private var _step = 1
  private var _isOpen = true

  def step: Int = _step
  def isOpen: Boolean = _isOpen

  def makeGuess(guess: BCNumber): Unit =
    if !isOpen then
      throw ClosedException

    outputPort.presentGuessHints(guess, secret getHints guess)

    if secret == guess then
      outputPort.presentSuccessMessage(step)
      if rankingRepo.wouldFitOnTop(step) then
        for
          player <- outputPort.promptPlayerNameToSaveScore()
          (ranking, position) <- rankingRepo add RankingRecord(step, player)
        do
          outputPort.presentUpdatedRanking(ranking, position)
      close()
    else
      _step += 1

  def close(): Unit =
    if _isOpen then
      _isOpen = false
      notifyRoundEnd()
