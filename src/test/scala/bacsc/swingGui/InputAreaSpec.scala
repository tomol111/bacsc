package bacsc.swingGui

import org.scalatest.flatspec.AnyFlatSpec
import scala.swing as sw

import bacsc.core


class InputAreaSpec extends AnyFlatSpec:

  behavior of "Input area"

  it should "notify user that input is empty" in {
    val commandRepository = core.CommandRepository(core.GameStub(), List())
    val inputArea = InputArea(passGuess = { _ => () }, commandRepository)
    inputArea.processTypedKey(sw.event.Key.Enter.id)
    assert(inputArea.popupLabel.text == "Input is empty")
  }

  it should "pass guess on confirmation" in {
    var wasGuessPassed = false
    val commandRepository = core.CommandRepository(core.GameStub(), List())
    val inputArea = InputArea(passGuess = { _ => wasGuessPassed = true }, commandRepository)
    inputArea.inputField.text = "1234"

    inputArea.processTypedKey(sw.event.Key.Enter.id)

    assert(wasGuessPassed)
    assert(inputArea.inputField.text == "")
  }

  it should "notify user when confirmed guess is invalid" in {
    val commandRepository = core.CommandRepository(core.GameStub(), List())
    val inputArea = InputArea(passGuess = { _ => ()}, commandRepository)
    inputArea.inputField.text = "1541"

    inputArea.processTypedKey(sw.event.Key.Enter.id)

    assert(inputArea.popupLabel.text == "Repeated digits: 1")
    assert(inputArea.inputField.text == "1541")
  }

  it should "clear popup label on every other key typed" in {
    val commandRepository = core.CommandRepository(core.GameStub(), List())
    val inputArea = InputArea(passGuess = { _ => ()}, commandRepository)
    inputArea.popupLabel.text = "MESSAGE"

    inputArea.processTypedKey(sw.event.Key.A.id)

    assert(inputArea.popupLabel.text == "")
  }

  it should "execute command" in {
    var wasCommandExecuted = false
    object FakeCommand extends core.Command("pqr"):
      override def execute(game: core.Game.Interface, args: List[String]): Either[core.Command.Exception, Unit] =
        wasCommandExecuted = true
        Right(())
    val commandRepository = core.CommandRepository(core.GameStub(), List(FakeCommand))
    val inputArea = InputArea(passGuess = { _ => () }, commandRepository)
    inputArea.inputField.text = ":pqr"

    inputArea.processTypedKey(sw.event.Key.Enter.id)

    assert(wasCommandExecuted)
    assert(inputArea.inputField.text == "")
  }

  it should "show command exception message" in {
    val commandRepository = core.CommandRepository(core.GameStub(), Nil)
    val inputArea = InputArea(passGuess = { _ => () }, commandRepository)
    inputArea.inputField.text = ":pqr"

    inputArea.processTypedKey(sw.event.Key.Enter.id)

    assert(inputArea.popupLabel.text == "Command 'pqr' has not been found")
  }


