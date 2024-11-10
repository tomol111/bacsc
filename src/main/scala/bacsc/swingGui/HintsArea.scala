package bacsc.swingGui

import scala.swing as sw
import javax.swing as jsw

import bacsc.core


class HintsArea(val tableModel: jsw.table.DefaultTableModel = HintsArea.TableModel()):
  def prepare(): Unit =
    tableModel.setDataVector(Array[Array[Any]](), Array[Any]("Guess", "Bulls", "Cows"))

  def add(guess: core.BCNumber, bullsCows: (Int, Int)): Unit =
    val (bulls, cows) = bullsCows
    tableModel.addRow(Array[Any](guess.digits, bulls, cows))

object HintsArea:
  class TableModel extends jsw.table.DefaultTableModel(0, 0):
    override def isCellEditable(row: Int, column: Int): Boolean = false
