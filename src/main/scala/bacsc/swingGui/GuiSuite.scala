package bacsc.swingGui

import javax.swing as jsw
import scala.swing as sw

import bacsc.core


class GuiSuite(game: core.Game.Interface):
  val hintsArea: HintsArea = HintsArea()
  val inputArea: InputArea = InputArea(game.currentRound.makeGuess(_), game.commandRepository)

  val frame: MainFrame = MainFrame(
    game.close, MenuBar(game), hintsArea.tableModel, inputArea.popupLabel, inputArea.inputField
  )
