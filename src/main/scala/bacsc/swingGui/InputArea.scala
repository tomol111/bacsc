package bacsc.swingGui

import scala.swing as sw

import bacsc.core


class InputArea(
    passGuess: core.BCNumber => Unit,
    commandRepository: core.CommandRepository,
):
  val popupLabel = new sw.Label:
    foreground = java.awt.Color.RED
    border = sw.Swing.EmptyBorder(5, 5, 5, 5)
    xLayoutAlignment = 0.5
    horizontalAlignment = sw.Alignment.Left
    maximumSize = new sw.Dimension(Short.MaxValue, preferredSize.height)

  val inputField = new sw.TextField:
    font = sw.Font(sw.Font.Monospaced, sw.Font.Style.Plain, 18)
    maximumSize = new sw.Dimension(Short.MaxValue, preferredSize.height)
    listenTo(keys)
    reactions += { case event: sw.event.KeyTyped => processTypedKey(event.char) }

  def processTypedKey(keyCode: Int): Unit =
    if keyCode == sw.event.Key.Enter.id
    then getInput()
      .flatMap(filterAndHandleCommand)
      .foreach(parseGuess)
    else clearMessage()

  private def getInput(): Option[String] =
    Some(inputField.text).filter(_.nonEmpty) orElse { showMessage("Input is empty"); None }

  private def consumeInput(): Unit =
    inputField.text = ""

  private def showMessage(message: String): Unit =
    popupLabel.text = message

  private def clearMessage(): Unit =
    popupLabel.text = ""

  private def parseGuess(input: String): Unit =
    core.BCNumber.make(input) match
      case Right(guess) =>
        consumeInput()
        passGuess(guess)
      case Left(exc) =>
        showMessage(exc.render())

  private def filterAndHandleCommand(input: String): Option[String] =
    if input startsWith core.Command.PREFIX.toString then
      consumeInput()
      commandRepository.executeCommandLine(input stripPrefix core.Command.PREFIX.toString)
        .left.foreach(exc => showMessage(exc.render()))
      None
    else
      Some(input)
