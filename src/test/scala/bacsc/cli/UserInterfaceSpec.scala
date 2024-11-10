package bacsc.cli

import org.scalatest.flatspec.AnyFlatSpec
import bacsc.core


class UserInterfaceSpec extends AnyFlatSpec:

  def capturingOutput[T](thunk: => T): (String, T) =
    val outputCapture = java.io.ByteArrayOutputStream()
    val result = Console.withOut(outputCapture) { thunk }
    (outputCapture.toString, result)

  def providingInput[T](input: String)(thunk: => T): (String, T) =
    val inputSource = java.io.StringReader(input)
    Console.withIn(inputSource) { capturingOutput { thunk } }


  behavior of "Cli initiator"

  it should "show game opening" in {
    val (message, _) = capturingOutput {
      UserInterface().init(core.GameStub())
    }
    assert(message == "BacSc\n=====\n")
  }


  behavior of "Cli game controller"

  it should "pass user guess" in {
    var passedGuess: String = null
    val fakeRound = new core.RoundStub:
      override def step: Int = 1
      override def makeGuess(guess: core.BCNumber): Unit =
        passedGuess = guess.digits
    val fakeGame = new core.GameStub:
      override def currentRound: core.Round.Model = fakeRound
      override def commandRepository: core.CommandRepository = core.CommandRepository(this, List())

    UserInterface(readLine = _ => Some("2345")).controlGame(fakeGame)

    assert(passedGuess == "2345")
  }

  it should "show prompt with information of current round step" in {
    val (message, _) = providingInput("\n") {
      UserInterface().takeUserInput(3, () => ())
    }

    assert(message == "[3] ")
  }

  ignore should "ensure new line after prompt when input was terminated" in { }
  ignore should "finish game when input was terminated" in { }

  it should "filter and execute command line" in {
    var commandExecuted = false
    val fakeCommand = new core.Command("xyz"):
      override def execute(game: core.Game.Interface, args: List[String]): Either[core.Command.Exception, Unit] =
        commandExecuted = true
        Right(())
    val commandRepository = core.CommandRepository(core.GameStub(), List(fakeCommand))

    val result = UserInterface().filterAndHandleCommand(":xyz", commandRepository)

    assert(commandExecuted)
    assert(result == None)
  }

  it should "print command exception message" in {
    val commandRepository = core.CommandRepository(core.GameStub(), List())

    val (message, result) = capturingOutput {
      UserInterface().filterAndHandleCommand(":xyz", commandRepository)
    }

    assert(message == "Command 'xyz' has not been found\n")
    assert(result == None)
  }

  it should "render and print number validation error if input is invalid" in {
    val (message, result) = capturingOutput {
      UserInterface().parseGuess("1231")
    }
    assert(message == "Repeated digits: 1\n")
    assert(result == None)
  }


  behavior of "Cli round output port"

  it should "show guess hints" in {
    val (message, _) = capturingOutput {
      UserInterface().roundOutputPort.presentGuessHints(core.BCNumber("3241"), (2, 1))
    }
    assert(message == "bulls: 2, cows: 1\n")
  }

  it should "show success message" in {
    val (message, _) = capturingOutput {
      UserInterface().roundOutputPort.presentSuccessMessage(4)
    }
    assert(message == "\n*** You guessed in 4 steps! ***\n\n")
  }

  it should "show updated ranking" in {
    val (message, _) = capturingOutput {
      UserInterface().roundOutputPort.presentUpdatedRanking(
        List(
          core.RankingRecord(3, core.Player("AAA")),
          core.RankingRecord(5, core.Player("BBB")),
          core.RankingRecord(6, core.Player("CCC")),
        ),
        1,
      )
    }
    assert(
      message ==
        """================Ranking=================
          | Pos. Steps Player
          |   1     3  AAA
          |>  2     5  BBB
          |   3     6  CCC
          |========================================
          |""".stripMargin
    )
  }

  behavior of "Cli round output port - prompting player for name to save score"

  it should "ask player for name" in {
    def fakeReadLine(message: String): Option[String] =
      assert(message == "Save score as: ")
      Some("Alice")

    val result = UserInterface(readLine = fakeReadLine).roundOutputPort.promptPlayerNameToSaveScore()

    assert(result contains "Alice")
  }

  it should "not yield player name if player terminates name input" in {
    val result = UserInterface(readLine = _ => None).roundOutputPort.promptPlayerNameToSaveScore()
    assert(result == None)
  }

  it should "show comunication if name is invalid and try again" in {
    val inputs = Iterator(Some("Bob\b"), Some("Bob"))
    val (message, result) = capturingOutput {
      UserInterface(readLine = _ => inputs.next)
        .roundOutputPort.promptPlayerNameToSaveScore()
    }
    assert(result == Some("Bob"))
    assert(message == "Name contains control characters\n")
  }

  it should "clear user input" in {
    val result = UserInterface(readLine = _ => Some(" Alice\t")).roundOutputPort.promptPlayerNameToSaveScore()
    assert(result contains "Alice")
  }


  behavior of "Cli game output port"

  it should "show bigger portion of text in pager" in {
    val (message, _) = capturingOutput {
      UserInterface().gameOutputPort.pager("A bunch\nof\ntext\n", "Some Title")
    }
    assert(
      message ==
        """===============Some Title===============
          |A bunch
          |of
          |text
          |========================================
          |""".stripMargin
    )
  }

  it should "expose round start" in {
    val (message, _) = capturingOutput {
      UserInterface().gameOutputPort.presentRound(core.RoundStub())
    }
    assert(message == "\n")
  }

  it should "show current ranking" in {
    val (message, _) = capturingOutput {
      UserInterface().gameOutputPort.presentRanking(
        List(
          core.RankingRecord(3, core.Player("AAA")),
          core.RankingRecord(5, core.Player("BBB")),
          core.RankingRecord(6, core.Player("CCC")),
        ),
      )
    }
    assert(
      message ==
        """================Ranking=================
          | Pos. Steps Player
          |   1     3  AAA
          |   2     5  BBB
          |   3     6  CCC
          |========================================
          |""".stripMargin
)
  }
