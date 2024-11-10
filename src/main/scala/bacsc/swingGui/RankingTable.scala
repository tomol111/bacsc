package bacsc.swingGui

import javax.swing as jsw
import scala.swing as sw

import bacsc.core


class RankingTable(model: jsw.table.AbstractTableModel, highligtRow: Option[Int] = None) extends sw.Table(model):
  override protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): sw.Component =
    val component = super.rendererComponent(isSelected, focused, row, column)
    component.background =
      if isSelected then
        selectionBackground
      else if highligtRow contains row then
        java.awt.Color.YELLOW
      else
        background
    component


class RankingTableModel(data: core.Ranking) extends jsw.table.AbstractTableModel:
  override def isCellEditable(row: Int, column: Int) = false
  def getRowCount: Int = data.length
  def getColumnCount: Int = 3

  override def getColumnName(column: Int): String =
    column match
      case 0 => "Pos."
      case 1 => "Steps"
      case 2 => "Player"

  def getValueAt(row: Int, col: Int): AnyRef =
    val result = col match
      case 0 => row + 1
      case 1 => data(row).steps
      case 2 => data(row).player
    result.asInstanceOf[AnyRef]
