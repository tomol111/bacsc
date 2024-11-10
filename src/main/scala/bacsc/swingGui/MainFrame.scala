package bacsc.swingGui

import scala.swing as sw

import bacsc.core


class MainFrame(
    onCloseOperation: () => Unit,
    menu: sw.MenuBar,
    hintsTableModel: javax.swing.table.TableModel,
    inputPopupLabel: sw.Label,
    inputField: sw.TextField,
) extends sw.Frame:
  title = "BacSc - Swing"
  menuBar = menu
  contents = new sw.BoxPanel(sw.Orientation.Vertical) {
    border = sw.Swing.EmptyBorder(10, 10, 10, 10)
    contents += sw.ScrollPane(sw.Table(hintsTableModel))
    contents += inputPopupLabel
    contents += inputField
  }

  override def closeOperation(): Unit =
    onCloseOperation()
