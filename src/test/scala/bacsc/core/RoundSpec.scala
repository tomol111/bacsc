package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec


class RoundSpec extends AnyFlatSpec:

  def dummyNotifyRoundEnd(): Unit = ()


  behavior of "Round"

  it should "present hints when making a guess" in {
    var hintsPresent = false
    object roundOutputPort extends DummyRoundOutputPort:
      override def presentGuessHints(guess: BCNumber, bullsCows: (Int, Int)): Unit =
        hintsPresent = true
    val round = Round(BCNumber("1234"), roundOutputPort, MemRankingRepo(), dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("4321"))

    assert(hintsPresent)
  }

  it should "count steps" in {
    val round = Round(BCNumber("1234"), DummyRoundOutputPort(), MemRankingRepo(), dummyNotifyRoundEnd)

    assert(round.step == 1)
    round.makeGuess(BCNumber("4321"))
    assert(round.step == 2)
    round.makeGuess(BCNumber("4231"))
    assert(round.step == 3)
    round.makeGuess(BCNumber("1234"))
    assert(round.step == 3)
  }

  it should "present success message when secret was guessed" in {
    var successMessagePresent = false
    object roundOutputPort extends DummyRoundOutputPort:
      override def presentSuccessMessage(stepsDone: Int): Unit = { successMessagePresent = true }
    val round = Round(BCNumber("1234"), roundOutputPort, MemRankingRepo(), dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("4321"))
    assert(!successMessagePresent)
    round.makeGuess(BCNumber("1234"))
    assert(successMessagePresent)
  }

  it should "prompt user's name to save score after succes message" in {
    object roundOutputPort extends DummyRoundOutputPort:
      override def promptPlayerNameToSaveScore(): Option[Player] = Some(Player("Cristine"))
    val rankingRepo = MemRankingRepo()
    val round = Round(BCNumber("1234"), roundOutputPort, rankingRepo, dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("4321"))
    assert(rankingRepo.data.isEmpty)
    round.makeGuess(BCNumber("1234"))
    assert(rankingRepo.data.nonEmpty)
  }

  it should "not save score if user's name was not provided" in {
    object roundOutputPort extends DummyRoundOutputPort:
      override def promptPlayerNameToSaveScore(): Option[Player] = None
    val rankingRepo = MemRankingRepo()
    val round = Round(BCNumber("1234"), roundOutputPort, rankingRepo, dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("1234"))
    assert(rankingRepo.data.isEmpty)
  }

  it should "not prompt name if score would not fit in top" in {
    object roundOutputPort extends DummyRoundOutputPort:
      override def promptPlayerNameToSaveScore(): Option[Player] = fail("name was prompted")
    object fakeRankingRepo extends MemRankingRepo:
      override def wouldFitOnTop(steps: Int, limit: Int): Boolean = false
    val round = Round(BCNumber("1234"), roundOutputPort, fakeRankingRepo, dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("1234"))
  }

  it should "present updated ranking" in {
    var rankingPresent = false
    object roundOutputPort extends DummyRoundOutputPort:
      override def promptPlayerNameToSaveScore(): Option[Player] = Some(Player("Adam"))
      override def presentUpdatedRanking(ranking: Ranking, position: Int): Unit =
        rankingPresent = true
    val round = Round(BCNumber("1234"), roundOutputPort, MemRankingRepo(), dummyNotifyRoundEnd)

    round.makeGuess(BCNumber("1234"))

    assert(rankingPresent)
  }

  it should "be closed after successive guess" in {
    val round = Round(BCNumber("1234"), DummyRoundOutputPort(), MemRankingRepo(), dummyNotifyRoundEnd)

    assert(round.isOpen)
    round.makeGuess(BCNumber("1234"))
    assert(!round.isOpen)
  }

  it can "be closed earlier" in {
    val round = Round(BCNumber("1234"), DummyRoundOutputPort(), MemRankingRepo(), dummyNotifyRoundEnd)
    round.close()
    assert(!round.isOpen)
  }

  it should "notify its ending once" in {
    var notifyCount = 0
    val round = Round(BCNumber("1234"), DummyRoundOutputPort(), MemRankingRepo(), () => notifyCount += 1)

    assert(notifyCount == 0)
    round.close()
    assert(notifyCount == 1)
    round.close()
    assert(notifyCount == 1)
  }


  behavior of "Closed round"

  it should "not allow guessing the secret" in {
    val round = Round(BCNumber("1234"), DummyRoundOutputPort(), MemRankingRepo(), dummyNotifyRoundEnd)

    round.close()

    assertThrows[Round.ClosedException.type] {
      round.makeGuess(BCNumber("1234"))
    }
  }
