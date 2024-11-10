package bacsc.swingGui

import javax.swing as jsw
import scala.swing as sw

import bacsc.core


class UserInterface(getGuiSuite: core.Game.Interface => GuiSuite = GuiSuite(_)) extends core.UserInterface:

  private var _suite: Option[GuiSuite] = None

  private def suite = _suite.getOrElse(throw Exception("Gui suite not set jet"))

  override def init(game: core.Game.Interface): Unit =
    _suite = Some(getGuiSuite(game))
    suite.frame.open()
    suite.inputArea.inputField.requestFocus()

  override def finish(): Unit =
    suite.frame.close()


  override object roundOutputPort extends core.Round.OutputPort:
    override def presentGuessHints(guess: core.BCNumber, bullsCows: (Int, Int)): Unit =
      suite.hintsArea.add(guess, bullsCows)

    override def presentSuccessMessage(stepsDone: Int): Unit =
      sw.Dialog.showMessage(suite.frame, s"You guessed in $stepsDone steps!", title="Round ended")

    override def promptPlayerNameToSaveScore(): Option[core.Player] =
      sw.Dialog.showInput(suite.frame, "Save score as:", title="Saving score", initial="").map(core.Player(_))

    override def presentUpdatedRanking(ranking: core.Ranking, position: Int): Unit =
      showRanking(ranking, highligthPosition=Some(position))


  override object gameOutputPort extends core.Game.OutputPort:

    override def pager(text: String, title: String = ""): Unit =
      val dialog = new sw.Dialog(suite.frame)
      dialog.title = title
      dialog.modal = true

      val boxPanel = sw.BoxPanel(sw.Orientation.Vertical)
      boxPanel.border = sw.Swing.EmptyBorder(10, 10, 10, 10)

      boxPanel.contents += sw.ScrollPane(
        new sw.TextArea(text):
          font = sw.Font(sw.Font.Monospaced, sw.Font.Style.Plain, 16)
          editable = false
      )
      boxPanel.contents += sw.Swing.VStrut(10)
      val closeButton = new sw.Button(
        sw.Action("Close")(dialog.close())
      ) {
        xLayoutAlignment = .5
        listenTo(keys)
        reactions += { case event: sw.event.KeyTyped if event.char == sw.event.Key.Enter.id => doClick() }
      }
      boxPanel.contents += closeButton

      dialog.contents = boxPanel

      closeButton.requestFocus()
      dialog.setLocationRelativeTo(suite.frame)
      dialog.open()

    override def presentRound(round: core.Round.Model): Unit =
      suite.hintsArea.prepare()

    override def presentRanking(ranking: core.Ranking): Unit =
      showRanking(ranking)


  def showRanking(ranking: core.Ranking, highligthPosition: Option[Int] = None): Unit =
    val dialog = new sw.Dialog(suite.frame)
    dialog.title = "Ranking"
    dialog.modal = true

    val boxPanel = sw.BoxPanel(sw.Orientation.Vertical)
    boxPanel.border = sw.Swing.EmptyBorder(10, 10, 10, 10)

    boxPanel.contents += sw.ScrollPane(RankingTable(RankingTableModel(ranking), highligtRow=highligthPosition))
    boxPanel.contents += sw.Swing.VStrut(10)
    val closeButton = new sw.Button(
      sw.Action("Close")(dialog.close())
    ) {
      xLayoutAlignment = .5
      listenTo(keys)
      reactions += { case event: sw.event.KeyTyped if event.char == sw.event.Key.Enter.id => doClick() }
    }
    boxPanel.contents += closeButton

    dialog.contents = boxPanel

    closeButton.requestFocus()
    dialog.setLocationRelativeTo(suite.frame)
    dialog.open()
